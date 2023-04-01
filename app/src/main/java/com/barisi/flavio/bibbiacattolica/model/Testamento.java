package com.barisi.flavio.bibbiacattolica.model;


import android.content.Context;

import com.barisi.flavio.bibbiacattolica.R;

import java.io.Serializable;

public class Testamento implements Serializable {
    private String codice;
    private int numeroLibri;

    public String getCodice() {
        return codice;
    }

    public String getDescrizione(Context c) {
        if (codice.equals("A")) {
            return c.getString(R.string.title_anticoTestamento);
        } else {
            return c.getString(R.string.title_nuovoTestamento);
        }
    }

    public int getNumeroLibri() {
        return numeroLibri;
    }

    public static Testamento getAntico() {
        Testamento t = new Testamento();
        t.codice = "A";
        t.numeroLibri = 46;
        return t;
    }

    public static Testamento getNuovo() {
        Testamento t = new Testamento();
        t.codice = "N";
        t.numeroLibri = 27;
        return t;
    }
}
