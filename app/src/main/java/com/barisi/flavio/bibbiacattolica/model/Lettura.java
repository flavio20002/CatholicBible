package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;
import java.util.List;

public class Lettura implements Serializable {
    private String descrizione;
    private String autore;
    private List<CapitoloLetture> capitoloLettura;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public List<CapitoloLetture> getCapitoloLettura() {
        return capitoloLettura;
    }

    public void setCapitoloLettura(List<CapitoloLetture> capitoloLettura) {
        this.capitoloLettura = capitoloLettura;
    }
}
