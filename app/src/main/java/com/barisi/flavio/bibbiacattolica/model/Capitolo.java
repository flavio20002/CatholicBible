package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;

public class Capitolo implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nomeLibro;
    private int numero;
    private String titolo;
    private String testoBreve;
    private String filtro;
    private int idLibro;

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getTestoBreve() {
        return testoBreve;
    }

    public void setTestoBreve(String testoBreve) {
        this.testoBreve = testoBreve;
    }

    public String getNomeLibro() {
        return nomeLibro;
    }

    public void setNomeLibro(String nomeLibro) {
        this.nomeLibro = nomeLibro;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    @Override
    public int compareTo(Object another) {
        if (another instanceof Capitolo) {
            Capitolo a = (Capitolo) another;
            if (this.getIdLibro() > a.getIdLibro()) {
                return 1;
            } else if (this.getIdLibro() < a.getIdLibro()) {
                return -1;
            } else {
                return Double.compare(this.getNumero(), a.getNumero());
            }
        }
        return 0;
    }
}
