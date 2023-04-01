package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;

public class Preghiera implements Serializable{

    private int id;
    private String nomepreghiera;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomepreghiera() {
        return nomepreghiera;
    }

    public void setNomepreghiera(String nomepreghiera) {
        this.nomepreghiera = nomepreghiera;
    }
}
