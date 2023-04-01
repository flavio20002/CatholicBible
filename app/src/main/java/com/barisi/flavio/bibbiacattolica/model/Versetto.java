package com.barisi.flavio.bibbiacattolica.model;

public class Versetto {
    private String numero;
    private String testo;

    public Versetto() {
        this.numero = "";
        this.testo = "";
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}
