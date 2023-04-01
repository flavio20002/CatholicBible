package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;

public class Segnalibro implements Serializable {

    private String idCapitolo;
    private String versetto;
    private String testoBreve;
    private String riferimento;
    private String nota;

    public String getIdCapitolo() {
        return idCapitolo;
    }

    public void setIdCapitolo(String idCapitolo) {
        this.idCapitolo = idCapitolo;
    }

    public String getVersetto() {
        return versetto;
    }

    public void setVersetto(String versetto) {
        this.versetto = versetto;
    }

    public String getTestoBreve() {
        return testoBreve;
    }

    public void setTestoBreve(String testoBreve) {
        this.testoBreve = testoBreve;
    }

    public String getRiferimento() {
        return riferimento;
    }

    public void setRiferimento(String riferimento) {
        this.riferimento = riferimento;
    }

    public String getNota() {return nota;}

    public void setNota(String nota) {this.nota = nota;}
}
