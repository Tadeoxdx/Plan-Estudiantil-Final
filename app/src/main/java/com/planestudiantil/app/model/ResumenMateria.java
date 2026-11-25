package com.planestudiantil.app.model;

public class ResumenMateria {
    private int materiaId;
    private String nombreMateria;
    private String colorHex;
    private int minutosTotales;
    private float porcentaje;

    public ResumenMateria(int materiaId, String nombreMateria, String colorHex,
                          int minutosTotales, float porcentaje) {
        this.materiaId = materiaId;
        this.nombreMateria = nombreMateria;
        this.colorHex = colorHex;
        this.minutosTotales = minutosTotales;
        this.porcentaje = porcentaje;
    }

    public int getMateriaId() {
        return materiaId;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public String getColorHex() {
        return colorHex;
    }

    public int getMinutosTotales() {
        return minutosTotales;
    }

    public float getPorcentaje() {
        return porcentaje;
    }

    public double getHorasTotales() {
        return minutosTotales / 60.0;
    }
}