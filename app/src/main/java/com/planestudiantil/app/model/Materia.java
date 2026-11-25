package com.planestudiantil.app.model;

public class Materia {
    private int id;
    private String nombre;
    private String colorHex;

    public Materia() {
    }

    public Materia(int id, String nombre, String colorHex) {
        this.id = id;
        this.nombre = nombre;
        this.colorHex = colorHex;
    }

    public Materia(String nombre, String colorHex) {
        this.nombre = nombre;
        this.colorHex = colorHex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    @Override
    public String toString() {
        return nombre;
    }
}