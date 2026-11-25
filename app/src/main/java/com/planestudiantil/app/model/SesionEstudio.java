package com.planestudiantil.app.model;

public class SesionEstudio {
    public static final String ESTADO_COMPLETADA = "Completada";
    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_REPROGRAMADA = "Reprogramada";

    private int id;
    private int materiaId;
    private String nombreMateria;
    private String colorMateria;
    private String fecha; // YYYY-MM-DD
    private String horaInicio; // HH:mm
    private String horaFin; // HH:mm
    private int duracionMinutos;
    private String estado;
    private String notas;

    public SesionEstudio() {
    }

    public SesionEstudio(int id, int materiaId, String nombreMateria, String colorMateria,
                         String fecha, String horaInicio, String horaFin,
                         int duracionMinutos, String estado, String notas) {
        this.id = id;
        this.materiaId = materiaId;
        this.nombreMateria = nombreMateria;
        this.colorMateria = colorMateria;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.duracionMinutos = duracionMinutos;
        this.estado = estado;
        this.notas = notas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(int materiaId) {
        this.materiaId = materiaId;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public String getColorMateria() {
        return colorMateria;
    }

    public void setColorMateria(String colorMateria) {
        this.colorMateria = colorMateria;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}