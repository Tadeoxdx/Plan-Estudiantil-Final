package com.planestudiantil.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.planestudiantil.app.model.Materia;
import com.planestudiantil.app.model.ResumenMateria;
import com.planestudiantil.app.model.SesionEstudio;

import java.util.ArrayList;
import java.util.List;

public class PlanEstudiantilDao {

    private final DatabaseHelper dbHelper;

    public PlanEstudiantilDao(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    // --- MATERIAS ---

    public long insertarMateria(Materia materia) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", materia.getNombre().trim());
        values.put("color_hex", materia.getColorHex());
        return db.insert(DatabaseHelper.TABLE_MATERIAS, null, values);
    }

    public List<Materia> obtenerMaterias() {
        List<Materia> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, nombre, color_hex FROM " + DatabaseHelper.TABLE_MATERIAS + " ORDER BY nombre ASC",
                null
        );
        if (cursor.moveToFirst()) {
            do {
                lista.add(new Materia(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public int actualizarMateria(Materia materia) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", materia.getNombre().trim());
        values.put("color_hex", materia.getColorHex());
        return db.update(
                DatabaseHelper.TABLE_MATERIAS,
                values,
                "id = ?",
                new String[]{String.valueOf(materia.getId())}
        );
    }

    public int eliminarMateria(int materiaId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_MATERIAS,
                "id = ?",
                new String[]{String.valueOf(materiaId)}
        );
    }

    // --- META SEMANAL ---

    public double obtenerMetaSemanalHoras() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double meta = 20.0;
        Cursor cursor = db.rawQuery(
                "SELECT meta_semanal_horas FROM " + DatabaseHelper.TABLE_META + " WHERE id = 1",
                null
        );
        if (cursor.moveToFirst()) {
            meta = cursor.getDouble(0);
        }
        cursor.close();
        return meta;
    }

    public int actualizarMetaSemanalHoras(double horas) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("meta_semanal_horas", horas);
        return db.update(DatabaseHelper.TABLE_META, values, "id = 1", null);
    }

    // --- SESIONES DE ESTUDIO ---

    public long insertarSesion(SesionEstudio sesion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("materia_id", sesion.getMateriaId());
        values.put("fecha", sesion.getFecha());
        values.put("hora_inicio", sesion.getHoraInicio());
        values.put("hora_fin", sesion.getHoraFin());
        values.put("duracion_minutos", sesion.getDuracionMinutos());
        values.put("estado", sesion.getEstado());
        values.put("notas", sesion.getNotas());
        return db.insert(DatabaseHelper.TABLE_SESIONES, null, values);
    }

    public int actualizarEstadoSesion(int sesionId, String nuevoEstado) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("estado", nuevoEstado);
        return db.update(
                DatabaseHelper.TABLE_SESIONES,
                values,
                "id = ?",
                new String[]{String.valueOf(sesionId)}
        );
    }

    public int eliminarSesion(int sesionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_SESIONES,
                "id = ?",
                new String[]{String.valueOf(sesionId)}
        );
    }

    public List<SesionEstudio> obtenerSesionesPorFecha(String fecha) {
        List<SesionEstudio> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT s.id, s.materia_id, m.nombre, m.color_hex, s.fecha, " +
                "s.hora_inicio, s.hora_fin, s.duracion_minutos, s.estado, s.notas " +
                "FROM " + DatabaseHelper.TABLE_SESIONES + " s " +
                "INNER JOIN " + DatabaseHelper.TABLE_MATERIAS + " m ON s.materia_id = m.id " +
                "WHERE s.fecha = ? ORDER BY s.hora_inicio ASC";
        Cursor cursor = db.rawQuery(sql, new String[]{fecha});
        if (cursor.moveToFirst()) {
            do {
                lista.add(new SesionEstudio(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getInt(7),
                        cursor.getString(8),
                        cursor.getString(9)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public List<SesionEstudio> obtenerSesionesEnRango(String fechaInicio, String fechaFin) {
        List<SesionEstudio> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT s.id, s.materia_id, m.nombre, m.color_hex, s.fecha, " +
                "s.hora_inicio, s.hora_fin, s.duracion_minutos, s.estado, s.notas " +
                "FROM " + DatabaseHelper.TABLE_SESIONES + " s " +
                "INNER JOIN " + DatabaseHelper.TABLE_MATERIAS + " m ON s.materia_id = m.id " +
                "WHERE s.fecha BETWEEN ? AND ? ORDER BY s.fecha ASC, s.hora_inicio ASC";
        Cursor cursor = db.rawQuery(sql, new String[]{fechaInicio, fechaFin});
        if (cursor.moveToFirst()) {
            do {
                lista.add(new SesionEstudio(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getInt(7),
                        cursor.getString(8),
                        cursor.getString(9)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // --- ESTADÍSTICAS Y REPORTES ---

    public int obtenerMinutosCompletadosEnRango(String fechaInicio, String fechaFin) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int totalMinutos = 0;
        String sql = "SELECT COALESCE(SUM(duracion_minutos), 0) FROM " + DatabaseHelper.TABLE_SESIONES +
                " WHERE estado = ? AND fecha BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(sql, new String[]{SesionEstudio.ESTADO_COMPLETADA, fechaInicio, fechaFin});
        if (cursor.moveToFirst()) {
            totalMinutos = cursor.getInt(0);
        }
        cursor.close();
        return totalMinutos;
    }

    public List<ResumenMateria> obtenerResumenPorMateria(String fechaInicio, String fechaFin) {
        List<ResumenMateria> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int totalGeneral = obtenerMinutosCompletadosEnRango(fechaInicio, fechaFin);

        String sql = "SELECT m.id, m.nombre, m.color_hex, COALESCE(SUM(s.duracion_minutos), 0) AS total_min " +
                "FROM " + DatabaseHelper.TABLE_MATERIAS + " m " +
                "LEFT JOIN " + DatabaseHelper.TABLE_SESIONES + " s " +
                "ON m.id = s.materia_id AND s.estado = ? AND s.fecha BETWEEN ? AND ? " +
                "GROUP BY m.id, m.nombre, m.color_hex " +
                "ORDER BY total_min DESC, m.nombre ASC";

        Cursor cursor = db.rawQuery(sql, new String[]{SesionEstudio.ESTADO_COMPLETADA, fechaInicio, fechaFin});
        if (cursor.moveToFirst()) {
            do {
                int minutos = cursor.getInt(3);
                float porcentaje = totalGeneral > 0 ? (minutos * 100f) / totalGeneral : 0f;
                lista.add(new ResumenMateria(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        minutos,
                        porcentaje
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}