package com.barisi.flavio.bibbiacattolica.model;

import java.util.HashMap;
import java.util.List;

public class TestoLettureGiornoConfronto {

    private String titoloPrimabibbia, titoloSecondabibbia, titoloTerzabibbia;
    private HashMap<String, List<Versetto>> testo1;
    private HashMap<String, List<Versetto>> testo2;
    private HashMap<String, List<Versetto>> testo3;
    private String direzionePrima;
    private String direzioneSeconda;
    private String direzioneTerza;

    public HashMap<String, List<Versetto>> getTesto1() {
        return testo1;
    }

    public void setTesto1(HashMap<String, List<Versetto>> testo1) {
        this.testo1 = testo1;
    }

    public HashMap<String, List<Versetto>> getTesto2() {
        return testo2;
    }

    public void setTesto2(HashMap<String, List<Versetto>> testo2) {
        this.testo2 = testo2;
    }

    public HashMap<String, List<Versetto>> getTesto3() {
        return testo3;
    }

    public void setTesto3(HashMap<String, List<Versetto>> testo3) {
        this.testo3 = testo3;
    }

    public String getTitoloPrimabibbia() {
        return titoloPrimabibbia;
    }

    public void setTitoloPrimabibbia(String titoloPrimabibbia) {
        this.titoloPrimabibbia = titoloPrimabibbia;
    }

    public String getTitoloSecondabibbia() {
        return titoloSecondabibbia;
    }

    public void setTitoloSecondabibbia(String titoloSecondabibbia) {
        this.titoloSecondabibbia = titoloSecondabibbia;
    }

    public String getTitoloTerzabibbia() {
        return titoloTerzabibbia;
    }

    public void setTitoloTerzabibbia(String titoloTerzabibbia) {
        this.titoloTerzabibbia = titoloTerzabibbia;
    }

    public String getDirezionePrima() {
        return direzionePrima;
    }

    public void setDirezionePrima(String direzionePrima) {
        this.direzionePrima = direzionePrima;
    }

    public String getDirezioneSeconda() {
        return direzioneSeconda;
    }

    public void setDirezioneSeconda(String direzioneSeconda) {
        this.direzioneSeconda = direzioneSeconda;
    }

    public String getDirezioneTerza() {
        return direzioneTerza;
    }

    public void setDirezioneTerza(String direzioneTerza) {
        this.direzioneTerza = direzioneTerza;
    }
}
