package com.barisi.flavio.bibbiacattolica.model;

public class Generico {

    private String codice;
    private String descrizione;

    public Generico(String codice, String descrizione) {
        this.codice = codice;
        this.descrizione = descrizione;
    }

    public String getCodice() {
        return codice;
    }

    @Override
    public String toString() {
        return descrizione;
    }
}
