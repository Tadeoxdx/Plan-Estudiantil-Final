package com.planestudiantil.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.planestudiantil.app.R;
import com.planestudiantil.app.data.PlanEstudiantilDao;
import com.planestudiantil.app.model.Materia;
import com.planestudiantil.app.model.ResumenMateria;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MasFragment extends Fragment {

    private static final String[] COLORES = {
            "#5E4AE3", "#1B7F4E", "#D97706", "#DC2626", "#0284C7", "#9333EA"
    };

    private EditText etMetaHoras, etNombreMateria;
    private Button btnGuardarMeta, btnCompartirReporte, btnAgregarMateria;
    private TextView tvContenidoReporte;
    private RecyclerView rvMaterias;

    private PlanEstudiantilDao dao;
    private MateriaAdapter adapter;
    private String textoReporteActual = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mas, container, false);

        dao = new PlanEstudiantilDao(requireContext());

        etMetaHoras = view.findViewById(R.id.etMetaHoras);
        btnGuardarMeta = view.findViewById(R.id.btnGuardarMeta);
        tvContenidoReporte = view.findViewById(R.id.tvContenidoReporte);
        btnCompartirReporte = view.findViewById(R.id.btnCompartirReporte);
        etNombreMateria = view.findViewById(R.id.etNombreMateria);
        btnAgregarMateria = view.findViewById(R.id.btnAgregarMateria);
        rvMaterias = view.findViewById(R.id.rvMaterias);

        rvMaterias.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MateriaAdapter(new MateriaAdapter.OnMateriaClickListener() {
            @Override
            public void onMateriaClick(ResumenMateria materia) {
                mostrarDialogoEditarMateria(materia);
            }

            @Override
            public void onMateriaLongClick(ResumenMateria materia) {
                confirmarEliminarMateria(materia);
            }
        });
        rvMaterias.setAdapter(adapter);

        btnGuardarMeta.setOnClickListener(v -> guardarMeta());
        btnAgregarMateria.setOnClickListener(v -> agregarMateria());
        btnCompartirReporte.setOnClickListener(v -> compartirReporte());

        cargarTodo();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarTodo();
    }

    private void cargarTodo() {
        double meta = dao.obtenerMetaSemanalHoras();
        etMetaHoras.setText(String.format(Locale.US, "%.1f", meta));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String inicioSemana = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String finSemana = sdf.format(cal.getTime());

        List<ResumenMateria> resumen = dao.obtenerResumenPorMateria(inicioSemana, finSemana);
        adapter.setItems(resumen);

        int minutosTotales = dao.obtenerMinutosCompletadosEnRango(inicioSemana, finSemana);
        double horasTotales = minutosTotales / 60.0;

        StringBuilder sb = new StringBuilder();
        sb.append("Periodo: ").append(inicioSemana).append(" a ").append(finSemana).append("\n");
        sb.append(String.format(Locale.getDefault(),
                "Horas completadas: %.1f h / %.1f h meta\n\n", horasTotales, meta));
        sb.append("Detalle por materia:\n");
        for (ResumenMateria rm : resumen) {
            sb.append(String.format(Locale.getDefault(),
                    "• %s: %.1f h (%.0f%%)\n",
                    rm.getNombreMateria(), rm.getHorasTotales(), rm.getPorcentaje()));
        }

        textoReporteActual = sb.toString().trim();
        tvContenidoReporte.setText(textoReporteActual);
    }

    private void guardarMeta() {
        String texto = etMetaHoras.getText().toString().trim();
        if (TextUtils.isEmpty(texto)) {
            Toast.makeText(requireContext(), "Ingresa una meta en horas", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double nuevaMeta = Double.parseDouble(texto);
            if (nuevaMeta <= 0) {
                Toast.makeText(requireContext(), "La meta debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }
            dao.actualizarMetaSemanalHoras(nuevaMeta);
            Toast.makeText(requireContext(), "Meta semanal actualizada", Toast.LENGTH_SHORT).show();
            cargarTodo();
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Número inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarMateria() {
        String nombre = etNombreMateria.getText().toString().trim();
        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(requireContext(), "Escribe el nombre de la materia", Toast.LENGTH_SHORT).show();
            return;
        }
        List<Materia> existentes = dao.obtenerMaterias();
        String color = COLORES[existentes.size() % COLORES.length];

        long id = dao.insertarMateria(new Materia(nombre, color));
        if (id > 0) {
            etNombreMateria.setText("");
            Toast.makeText(requireContext(), "Materia añadida", Toast.LENGTH_SHORT).show();
            cargarTodo();
        } else {
            Toast.makeText(requireContext(), "No se pudo añadir (¿nombre repetido?)", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoEditarMateria(ResumenMateria item) {
        EditText input = new EditText(requireContext());
        input.setText(item.getNombreMateria());
        new AlertDialog.Builder(requireContext())
                .setTitle("Editar materia")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(nuevoNombre)) {
                        dao.actualizarMateria(new Materia(
                                item.getMateriaId(), nuevoNombre, item.getColorHex()
                        ));
                        cargarTodo();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarMateria(ResumenMateria item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar materia")
                .setMessage("Se eliminará " + item.getNombreMateria() + " y sus sesiones asociadas.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dao.eliminarMateria(item.getMateriaId());
                    cargarTodo();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void compartirReporte() {
        if (TextUtils.isEmpty(textoReporteActual)) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Reporte Plan Estudiantil");
        intent.putExtra(Intent.EXTRA_TEXT, textoReporteActual);
        startActivity(Intent.createChooser(intent, "Compartir reporte"));
    }
}