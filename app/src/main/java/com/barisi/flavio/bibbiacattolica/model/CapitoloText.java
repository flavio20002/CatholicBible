package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;

public class CapitoloText extends Capitolo {

    private static final long serialVersionUID = 1L;

    private String testo;

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}
