package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCategoria;
    private String desGategoria;

    private List<Libro> listaLibri;

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDesGategoria() {
        return desGategoria;
    }

    public void setDesGategoria(String desGategoria) {
        this.desGategoria = desGategoria;
    }

    public List<Libro> getListaLibri() {
        return listaLibri;
    }

    public void setListaLibri(List<Libro> listaLibri) {
        this.listaLibri = listaLibri;
    }

    public void addLibro(Libro libro) {
        if (this.listaLibri == null) {
            this.listaLibri = new ArrayList<Libro>();
        }
        this.listaLibri.add(libro);
    }

}
