package com.barisi.flavio.bibbiacattolica.model;

import java.util.HashMap;

public class TestoCapitoloPerConfronto {

    private String nomeLibro;
    private int numero;
    private String titoloPrimabibbia, titoloSecondabibbia, titoloTerzabibbia;
    private HashMap<Integer, Versetto> testo1;
    private HashMap<Integer, Versetto> testo2;
    private HashMap<Integer, Versetto> testo3;
    private String direzionePrima;
    private String direzioneSeconda;
    private String direzioneTerza;

    public String getNomeLibro() {
        return nomeLibro;
    }

    public void setNomeLibro(String nomeLibro) {
        this.nomeLibro = nomeLibro;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public HashMap<Integer, Versetto> getTesto1() {
        return testo1;
    }

    public void setTesto1(HashMap<Integer, Versetto> testo1) {
        this.testo1 = testo1;
    }

    public HashMap<Integer, Versetto> getTesto2() {
        return testo2;
    }

    public void setTesto2(HashMap<Integer, Versetto> testo2) {
        this.testo2 = testo2;
    }

    public HashMap<Integer, Versetto> getTesto3() {
        return testo3;
    }

    public void setTesto3(HashMap<Integer, Versetto> testo3) {
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
