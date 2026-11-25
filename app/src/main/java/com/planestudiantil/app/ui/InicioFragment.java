package com.planestudiantil.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.planestudiantil.app.R;
import com.planestudiantil.app.data.PlanEstudiantilDao;
import com.planestudiantil.app.model.SesionEstudio;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InicioFragment extends Fragment {

    private TextView tvMetaProgreso, tvProximaSesion;
    private ProgressBar progressMetaSemanal;
    private Button btnNuevaSesion;
    private RecyclerView rvSesionesHoy;

    private PlanEstudiantilDao dao;
    private SesionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        dao = new PlanEstudiantilDao(requireContext());

        tvMetaProgreso = view.findViewById(R.id.tvMetaProgreso);
        tvProximaSesion = view.findViewById(R.id.tvProximaSesion);
        progressMetaSemanal = view.findViewById(R.id.progressMetaSemanal);
        btnNuevaSesion = view.findViewById(R.id.btnNuevaSesion);
        rvSesionesHoy = view.findViewById(R.id.rvSesionesHoy);

        rvSesionesHoy.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SesionAdapter(new SesionAdapter.OnSesionClickListener() {
            @Override
            public void onSesionClick(SesionEstudio sesion) {
                mostrarOpcionesEstado(sesion);
            }

            @Override
            public void onSesionLongClick(SesionEstudio sesion) {
                confirmarEliminarSesion(sesion);
            }
        });
        rvSesionesHoy.setAdapter(adapter);

        btnNuevaSesion.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegistrarSesionActivity.class);
            startActivity(intent);
        });

        cargarDatosInicio();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarDatosInicio();
    }

    private void cargarDatosInicio() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String hoy = sdf.format(new Date());

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String inicioSemana = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String finSemana = sdf.format(cal.getTime());

        int minutosCompletados = dao.obtenerMinutosCompletadosEnRango(inicioSemana, finSemana);
        double horasCompletadas = minutosCompletados / 60.0;
        double metaHoras = dao.obtenerMetaSemanalHoras();

        tvMetaProgreso.setText(String.format(Locale.getDefault(), "%.1f / %.1f horas",
                horasCompletadas, metaHoras));

        int porcentaje = metaHoras > 0 ? (int) Math.min(100, (horasCompletadas * 100.0) / metaHoras) : 0;
        progressMetaSemanal.setProgress(porcentaje);

        List<SesionEstudio> sesionesHoy = dao.obtenerSesionesPorFecha(hoy);
        adapter.setSesiones(sesionesHoy);

        SesionEstudio proxima = null;
        for (SesionEstudio s : sesionesHoy) {
            if (!SesionEstudio.ESTADO_COMPLETADA.equals(s.getEstado())) {
                proxima = s;
                break;
            }
        }

        if (proxima != null) {
            tvProximaSesion.setText(proxima.getNombreMateria() + " · Hoy " +
                    proxima.getHoraInicio() + " - " + proxima.getHoraFin());
        } else {
            tvProximaSesion.setText("Sin sesiones pendientes hoy");
        }
    }

    private void mostrarOpcionesEstado(SesionEstudio sesion) {
        String[] estados = {
                SesionEstudio.ESTADO_COMPLETADA,
                SesionEstudio.ESTADO_PENDIENTE,
                SesionEstudio.ESTADO_REPROGRAMADA
        };
        new AlertDialog.Builder(requireContext())
                .setTitle("Cambiar estado de sesión")
                .setItems(estados, (dialog, which) -> {
                    dao.actualizarEstadoSesion(sesion.getId(), estados[which]);
                    Toast.makeText(requireContext(), "Estado actualizado", Toast.LENGTH_SHORT).show();
                    cargarDatosInicio();
                })
                .show();
    }

    private void confirmarEliminarSesion(SesionEstudio sesion) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar sesión")
                .setMessage("¿Deseas eliminar la sesión de " + sesion.getNombreMateria() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dao.eliminarSesion(sesion.getId());
                    cargarDatosInicio();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}