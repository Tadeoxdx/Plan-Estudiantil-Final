package com.planestudiantil.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import android.widget.CalendarView;
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
import java.util.Locale;

public class CalendarioFragment extends Fragment {

    private CalendarView calendarView;
    private TextView tvFechaSeleccionada;
    private RecyclerView rvSesionesFecha;

    private PlanEstudiantilDao dao;
    private SesionAdapter adapter;
    private String fechaActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        dao = new PlanEstudiantilDao(requireContext());
        calendarView = view.findViewById(R.id.calendarView);
        tvFechaSeleccionada = view.findViewById(R.id.tvFechaSeleccionada);
        rvSesionesFecha = view.findViewById(R.id.rvSesionesFecha);

        rvSesionesFecha.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SesionAdapter(new SesionAdapter.OnSesionClickListener() {
            @Override
            public void onSesionClick(SesionEstudio sesion) {
                mostrarOpcionesEstado(sesion);
            }

            @Override
            public void onSesionLongClick(SesionEstudio sesion) {
                dao.eliminarSesion(sesion.getId());
                Toast.makeText(requireContext(), "Sesión eliminada", Toast.LENGTH_SHORT).show();
                cargarSesionesDeFecha(fechaActual);
            }
        });
        rvSesionesFecha.setAdapter(adapter);

        fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(calendarView.getDate());
        cargarSesionesDeFecha(fechaActual);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(cal.getTime());
            cargarSesionesDeFecha(fechaActual);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fechaActual != null) {
            cargarSesionesDeFecha(fechaActual);
        }
    }

    private void cargarSesionesDeFecha(String fecha) {
        tvFechaSeleccionada.setText("Sesiones del " + fecha);
        adapter.setSesiones(dao.obtenerSesionesPorFecha(fecha));
    }

    private void mostrarOpcionesEstado(SesionEstudio sesion) {
        String[] estados = {
                SesionEstudio.ESTADO_COMPLETADA,
                SesionEstudio.ESTADO_PENDIENTE,
                SesionEstudio.ESTADO_REPROGRAMADA
        };
        new AlertDialog.Builder(requireContext())
                .setTitle("Cambiar estado")
                .setItems(estados, (dialog, which) -> {
                    dao.actualizarEstadoSesion(sesion.getId(), estados[which]);
                    cargarSesionesDeFecha(fechaActual);
                })
                .show();
    }
}