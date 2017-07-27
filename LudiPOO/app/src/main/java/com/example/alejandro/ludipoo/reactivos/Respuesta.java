package com.example.alejandro.ludipoo.reactivos;

import java.io.Serializable;

/**
 * Created by Luis on 11-May-17.
 */

public class Respuesta implements Serializable{
    private boolean acierto;
    private String fecha;
    private String idPregunta;
    private String idUser;
    private int unidad;
    private int respuesta;

    public Respuesta(){}

    public Respuesta(boolean acierto, String fecha, String idPregunta, String idUser, int respuesta, int unidad) {
        this.acierto = acierto;
        this.fecha = fecha;
        this.idPregunta = idPregunta;
        this.idUser = idUser;
        this.respuesta = respuesta;
        this.unidad = unidad;
    }

    public boolean isAcierto() {
        return acierto;
    }

    public void setAcierto(boolean acierto) {
        this.acierto = acierto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(String idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public int getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(int respuesta) {
        this.respuesta = respuesta;
    }

    public int getUnidad() {
        return unidad;
    }

    public void setUnidad(int unidad) {
        this.unidad = unidad;
    }
}
