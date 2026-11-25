package com.planestudiantil.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "plan_estudiantil.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_MATERIAS = "materias";
    public static final String TABLE_SESIONES = "sesiones_estudio";
    public static final String TABLE_META = "configuracion_meta";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMaterias = "CREATE TABLE " + TABLE_MATERIAS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL UNIQUE, " +
                "color_hex TEXT DEFAULT '#5E4AE3'" +
                ")";

        String createSesiones = "CREATE TABLE " + TABLE_SESIONES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "materia_id INTEGER NOT NULL, " +
                "fecha TEXT NOT NULL, " +
                "hora_inicio TEXT NOT NULL, " +
                "hora_fin TEXT NOT NULL, " +
                "duracion_minutos INTEGER NOT NULL, " +
                "estado TEXT NOT NULL DEFAULT 'Pendiente', " +
                "notas TEXT, " +
                "FOREIGN KEY (materia_id) REFERENCES " + TABLE_MATERIAS + "(id) ON DELETE CASCADE" +
                ")";

        String createMeta = "CREATE TABLE " + TABLE_META + " (" +
                "id INTEGER PRIMARY KEY CHECK (id = 1), " +
                "meta_semanal_horas REAL NOT NULL DEFAULT 20.0" +
                ")";

        db.execSQL(createMaterias);
        db.execSQL(createSesiones);
        db.execSQL(createMeta);

        ContentValues metaInicial = new ContentValues();
        metaInicial.put("id", 1);
        metaInicial.put("meta_semanal_horas", 20.0);
        db.insert(TABLE_META, null, metaInicial);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESIONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATERIAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_META);
        onCreate(db);
    }
}