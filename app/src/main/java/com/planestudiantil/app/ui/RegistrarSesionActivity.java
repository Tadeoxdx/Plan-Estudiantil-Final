package com.planestudiantil.app.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.planestudiantil.app.R;
import com.planestudiantil.app.data.PlanEstudiantilDao;
import com.planestudiantil.app.model.Materia;
import com.planestudiantil.app.model.SesionEstudio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistrarSesionActivity extends AppCompatActivity {

    private Spinner spinnerMateria, spinnerEstado;
    private EditText etFecha, etHoraInicio, etHoraFin, etNotas;
    private Button btnGuardarSesion;

    private PlanEstudiantilDao dao;
    private List<Materia> listaMaterias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_sesion);

        dao = new PlanEstudiantilDao(this);

        spinnerMateria = findViewById(R.id.spinnerMateria);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        etFecha = findViewById(R.id.etFecha);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFin = findViewById(R.id.etHoraFin);
        etNotas = findViewById(R.id.etNotas);
        btnGuardarSesion = findViewById(R.id.btnGuardarSesion);

        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        etFecha.setText(fechaHoy);
        etHoraInicio.setText("10:00");
        etHoraFin.setText("11:30");

        cargarMaterias();
        cargarEstados();

        btnGuardarSesion.setOnClickListener(v -> guardarSesion());
    }

    private void cargarMaterias() {
        listaMaterias = dao.obtenerMaterias();
        if (listaMaterias.isEmpty()) {
            Toast.makeText(this, "Primero agrega al menos una materia en la pestaña Más", Toast.LENGTH_LONG).show();
            btnGuardarSesion.setEnabled(false);
            return;
        }
        ArrayAdapter<Materia> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, listaMaterias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMateria.setAdapter(adapter);
    }

    private void cargarEstados() {
        String[] estados = {
                SesionEstudio.ESTADO_COMPLETADA,
                SesionEstudio.ESTADO_PENDIENTE,
                SesionEstudio.ESTADO_REPROGRAMADA
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, estados
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);
    }

    private void guardarSesion() {
        if (listaMaterias == null || listaMaterias.isEmpty()) {
            Toast.makeText(this, "No hay materias disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        Materia materiaSeleccionada = (Materia) spinnerMateria.getSelectedItem();
        String fecha = etFecha.getText().toString().trim();
        String horaInicio = etHoraInicio.getText().toString().trim();
        String horaFin = etHoraFin.getText().toString().trim();
        String estado = (String) spinnerEstado.getSelectedItem();
        String notas = etNotas.getText().toString().trim();

        if (TextUtils.isEmpty(fecha) || TextUtils.isEmpty(horaInicio) || TextUtils.isEmpty(horaFin)) {
            Toast.makeText(this, "Completa la fecha y las horas de inicio y fin", Toast.LENGTH_SHORT).show();
            return;
        }

        int duracionMinutos = calcularMinutos(horaInicio, horaFin);
        if (duracionMinutos <= 0) {
            Toast.makeText(this, "La hora de fin debe ser posterior a la hora de inicio (HH:mm)", Toast.LENGTH_SHORT).show();
            return;
        }

        SesionEstudio sesion = new SesionEstudio();
        sesion.setMateriaId(materiaSeleccionada.getId());
        sesion.setFecha(fecha);
        sesion.setHoraInicio(horaInicio);
        sesion.setHoraFin(horaFin);
        sesion.setDuracionMinutos(duracionMinutos);
        sesion.setEstado(estado);
        sesion.setNotas(notas);

        long id = dao.insertarSesion(sesion);
        if (id > 0) {
            Toast.makeText(this, "Sesión guardada (" + duracionMinutos + " min)", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar la sesión", Toast.LENGTH_SHORT).show();
        }
    }

    private int calcularMinutos(String inicio, String fin) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date dInicio = sdf.parse(inicio);
            Date dFin = sdf.parse(fin);
            if (dInicio == null || dFin == null) return -1;
            long diffMillis = dFin.getTime() - dInicio.getTime();
            return (int) (diffMillis / (1000 * 60));
        } catch (ParseException e) {
            return -1;
        }
    }
}