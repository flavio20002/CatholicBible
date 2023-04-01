package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;
import java.util.List;

public class CapitoloLetture implements Serializable {
    private int capitolo;
    private List<Quadruple<String, String, String, String>> versetti;

    public int getCapitolo() {
        return capitolo;
    }

    public void setCapitolo(int capitolo) {
        this.capitolo = capitolo;
    }

    public List<Quadruple<String, String, String, String>> getVersetti() {
        return versetti;
    }

    public void setVersetti(List<Quadruple<String, String, String, String>> versetti) {
        this.versetti = versetti;
    }
}