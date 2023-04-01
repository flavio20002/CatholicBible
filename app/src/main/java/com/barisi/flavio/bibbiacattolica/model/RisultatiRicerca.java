package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RisultatiRicerca implements Serializable {

    public RisultatiRicerca() {
        this.setRowIds(new ArrayList<Integer>());
        this.setFiltro("");
    }

    private static final long serialVersionUID = 1L;

    private List<Integer> rowIds;
    private String filtro;

    public List<Integer> getRowIds() {
        return rowIds;
    }

    public void setRowIds(List<Integer> rowIds) {
        this.rowIds = rowIds;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}
