package com.example.alejandro.ludipoo.reactivos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luis on 08-May-17.
 */

public class Reactivo implements Serializable {

    private String pregunta;
    private List<String> respuestas;
    private int indexCorrecto, unidad;

    public Reactivo(){}

    public Reactivo(String pregunta, String resp1, String resp2, String resp3, String resp4, int indexCorrecto, int unidad) {
        this.pregunta = pregunta;
        this.respuestas = new ArrayList<>();
        respuestas.add(resp1);
        respuestas.add(resp2);
        respuestas.add(resp3);
        respuestas.add(resp4);
        this.indexCorrecto = indexCorrecto;
        this.unidad = unidad;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public int getIndexCorrecto() {
        return indexCorrecto;
    }

    public void setIndexCorrecto(int indexCorrecto) {
        this.indexCorrecto = indexCorrecto;
    }

    public List<String> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<String> respuestas) {
        this.respuestas = respuestas;
    }

    public int getUnidad() {
        return unidad;
    }

    public void setUnidad(int unidad) {
        this.unidad = unidad;
    }
}
