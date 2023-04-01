package com.barisi.flavio.bibbiacattolica.gui;


import android.content.Context;

import com.barisi.flavio.bibbiacattolica.Preferenze;

public class Css {

    public static String getMostraVersettiStyle(boolean mostraVersetti) {
        String mostraVersettiStyle;
        if (mostraVersetti) {
            mostraVersettiStyle = "<style>sup {display: inline;}</style>";
        } else {
            mostraVersettiStyle = "<style>sup {display: none;}</style>";
        }
        return mostraVersettiStyle;
    }

    public static String getColoreStyle(Context c, int modalitaNotte) {
        int coloreEvidenziazione = Preferenze.ottieniColoreEvidenziazione(c);
        String colorString = "#" + String.format("%X", coloreEvidenziazione).substring(2);
        String coloreStyle;
        if (modalitaNotte == 0) {
            coloreStyle = "<style>body {color: black;} #highlighted2 { background-color: " + colorString + "; color:#000000;}</style>";
        } else if (modalitaNotte == 1 || modalitaNotte == 3) {
            coloreStyle = "<style>body {color: white;} sup {color: #FFD700;} .titoli {color: #FFD700;} th {color: #FFD700;} .bookmark {background-image: url(\"bookmark2.png\");} #highlighted2 { background-color: " + colorString + "; color:#000000;}</style>";
        } else {
            coloreStyle = "<style>body {color: #645032;} #highlighted2 { background-color: " + colorString + "; color:#000000;}</style>";
        }
        return coloreStyle;
    }

    public static String getACapoStyle(int aCapo) {
        String aCapoStyle;
        if (aCapo == 0) {
            aCapoStyle = "<style>br {display: inline;}</style>";
        } else {
            aCapoStyle = "<style>br {display: none;}</style>";
        }
        return aCapoStyle;
    }

    public static String getTestoGiustificatoStyle(int testoGiustificato) {
        String testoGiustificatoStyle;
        if (testoGiustificato == 0) {
            testoGiustificatoStyle = "<style>body {text-align:justify;} .testosapienziali {text-align:left;} .testosinistra {text-align:left;}</style>";
        } else if (testoGiustificato == 1) {
            testoGiustificatoStyle = "<style>body {text-align:left;} .testosapienziali {text-align:left;} .testosinistra {text-align:left;}</style>";
        } else {
            testoGiustificatoStyle = "<style>body {text-align:justify;} .testosapienziali {text-align:justify;} .testosinistra {text-align:justify;}</style>";
        }
        return testoGiustificatoStyle;
    }


}
