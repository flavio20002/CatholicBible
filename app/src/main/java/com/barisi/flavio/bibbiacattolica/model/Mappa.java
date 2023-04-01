package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;

public class Mappa implements Serializable {

    private int immagine;
    private String nomeMappa;
    private String nomeBreveMappa;
    private String anno;

    public int getImmagine() {
        return immagine;
    }

    public void setImmagine(int immagine) {
        this.immagine = immagine;
    }

    public String getNomeMappa() {
        return nomeMappa;
    }

    public void setNomeMappa(String nomeMappa) {
        this.nomeMappa = nomeMappa;
    }

    public String getNomeBreveMappa() {
        return nomeBreveMappa;
    }

    public void setNomeBreveMappa(String nomeBreveMappa) {
        this.nomeBreveMappa = nomeBreveMappa;
    }

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }
}
