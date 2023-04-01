package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;

public class Libro implements Serializable {

    private static final long serialVersionUID = 1L;

    private String categoria;
    private String codLibro;
    private String desLibro;
    private int numeroCapitoli;
    private String abbreviazione;

    public String getCodLibro() {
        return codLibro;
    }

    public void setCodLibro(String codLibro) {
        this.codLibro = codLibro;
    }

    public String getDesLibro() {
        return desLibro;
    }

    public void setDesLibro(String desLibro) {
        this.desLibro = desLibro;
    }

    public int getNumeroCapitoli() {
        return numeroCapitoli;
    }

    public void setNumeroCapitoli(int numeroCapitoli) {
        this.numeroCapitoli = numeroCapitoli;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getAbbreviazione() {
        return abbreviazione;
    }

    public void setAbbreviazione(String abbreviazione) {
        this.abbreviazione = abbreviazione;
    }
}
