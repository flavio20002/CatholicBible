package com.barisi.flavio.bibbiacattolica.database;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.barisi.flavio.bibbiacattolica.Cache;
import com.barisi.flavio.bibbiacattolica.Costanti;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.model.Bibbia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class Inizializzazione {

    private static final String DB_NAME = "database.db";
    private static final String ESPANSIONE = "com.barisi.flavio.bibbiacattolica.espansione1";


    public static void inizializza(Context c) {
        Preferenze.aggiornaVersioneApplicativo(c, getVersioneCorrente(c));
        Preferenze.aggiornaVersioneEspansione(c, getVersioneEspansione(c));
        try {
            caricaDatabaseDaAsset(c, DB_NAME);
            HashMap<String, Bibbia> listaBibbie = Cache.getBibbie(c);
            //Pulisce tutti i database e carica quello nelle preferenze
            for (Bibbia file : listaBibbie.values()) {
                cancellaDatabase(c, file.getFile());
            }
            caricaDatabaseSeNecessario(c, Preferenze.ottieniVersioneBibbia(c));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Cache.clearCache();
    }

    public static void eliminaBibbieCache(Context c) {
        try {
            HashMap<String, Bibbia> listaBibbie = Cache.getBibbie(c);
            String nomeDatabaseVersioneUtilizzata = DatabaseHelper.databaseName(c, Preferenze.ottieniVersioneBibbia(c));
            for (Bibbia file : listaBibbie.values()) {
                if (!file.getFile().equals(nomeDatabaseVersioneUtilizzata)) {
                    cancellaDatabase(c, file.getFile());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Long spazioOccupatoCache(Context c) {
        long result = 0;
        try {
            HashMap<String, Bibbia> listaBibbie = Cache.getBibbie(c);
            String nomeDatabaseVersioneUtilizzata = DatabaseHelper.databaseName(c, Preferenze.ottieniVersioneBibbia(c));
            for (Bibbia file : listaBibbie.values()) {
                if (!file.getFile().equals(nomeDatabaseVersioneUtilizzata)) {
                    File dbFile = c.getDatabasePath(file.getFile());
                    result += dbFile.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result / 1024 / 1024;
    }

    private static int getVersioneCorrente(Context c) {
        int versionCode;
        try {
            versionCode = c.getPackageManager().getPackageInfo(
                    c.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            return -1;
        }
        Log.d("Debug", String.valueOf(versionCode));
        return versionCode;
    }

    private static int getVersioneEspansione(Context c) {
        int versionCode;
        try {
            versionCode = c.getPackageManager().getPackageInfo(
                    ESPANSIONE, 0).versionCode;
        } catch (NameNotFoundException e) {
            return -1;
        }
        Log.d("Debug", String.valueOf(versionCode));
        return versionCode;
    }

    private static boolean seDatabaseCaricato(Context c, String nomeDb) {
        return c.getDatabasePath(nomeDb).exists();
    }

    private static void caricaDatabaseDaAsset(Context c, String nomeDb) throws Exception {
        InputStream myInput = c.getAssets().open(nomeDb);
        DatabaseHelper helper = new DatabaseHelper(c);
        SQLiteDatabase database = helper.getReadableDatabase();
        String dbFile = database.getPath();
        database.close();
        FileOutputStream myOutput = new FileOutputStream(dbFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private static void caricaDatabaseBibbiaDaAsset(Context c, String fileDb) throws Exception {
        InputStream myInput = c.getAssets().open(fileDb);
        DatabaseHelper helper = new DatabaseHelper(c, fileDb, 0);
        SQLiteDatabase database = helper.getReadableDatabase();
        String dbFile = database.getPath();
        database.close();
        FileOutputStream myOutput = new FileOutputStream(dbFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private static void caricaDatabaseDaEspansione(Context c, String nomeEspansione, String fileDb) throws Exception {
        Resources res = c.getPackageManager().getResourcesForApplication(nomeEspansione);
        InputStream myInput = res.getAssets().open(fileDb);
        DatabaseHelper helper = new DatabaseHelper(c, fileDb,0);
        SQLiteDatabase database = helper.getReadableDatabase();
        String dbFile = database.getPath();
        database.close();
        FileOutputStream myOutput = new FileOutputStream(dbFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private static void cancellaDatabase(Context c, String nomeDb) throws Exception {
        c.deleteDatabase(nomeDb);
    }

    public static void caricaDatabaseSeNecessario(Context c, String versioneBibbia) throws Exception {
        try {
            String nomeDatabase = DatabaseHelper.databaseName(c, versioneBibbia);
            if (!seDatabaseCaricato(c, nomeDatabase)) {
                String espansione = DatabaseHelper.databaseAddOn(c, versioneBibbia);
                int minVersioneEspansione = DatabaseHelper.databaseMinVersioneEspansione(c, versioneBibbia);
                if (Regex.stringaNonVuota(espansione)) {
                    int espansioneInstalled = isAppInstalled(c, espansione, minVersioneEspansione);
                    if (espansioneInstalled == Costanti.ESPANSIONE_INSTALLATA) {
                        caricaDatabaseDaEspansione(c, espansione, nomeDatabase);
                    } else { //In caso di spostamento Bibbia su espansione o eliminazione espansione
                        String bibbiaDefault = c.getString(R.string.imp_vers_bibbia_default);
                        Preferenze.salvaVersioneBibbia(c, bibbiaDefault);
                        nomeDatabase = DatabaseHelper.databaseName(c, bibbiaDefault);
                        caricaDatabaseBibbiaDaAsset(c, nomeDatabase);
                    }
                } else {
                    caricaDatabaseBibbiaDaAsset(c, nomeDatabase);
                }
                Log.d("Inizializzazione", "Caricato database " + nomeDatabase);
            }
        } catch (Exception e) {
            // In caso di eccezioni, carico la bibbia di default
            String bibbiaDefault = c.getString(R.string.imp_vers_bibbia_default);
            Preferenze.salvaVersioneBibbia(c, bibbiaDefault);
            String nomeDatabase = DatabaseHelper.databaseName(c, bibbiaDefault);
            caricaDatabaseBibbiaDaAsset(c, nomeDatabase);
        }
    }

    public static boolean daInizializzare(Context c) {
        int versionCodeCorrente = getVersioneCorrente(c);
        int versionCodeLetta = Preferenze.versioneApplicativo(c);
        int versionCodeCorrenteEspansione = getVersioneEspansione(c);
        int versionCodeLettaEspansione = Preferenze.versioneEspansione(c);
        boolean espansioneCancellata = versionCodeCorrenteEspansione == -1 && versionCodeLettaEspansione > -1;
        boolean espansioneVecchia = versionCodeLettaEspansione < versionCodeCorrenteEspansione;
        ServiziDatabase db = new ServiziDatabase(c);
        File dbFile = c.getDatabasePath(DB_NAME);
        return (!dbFile.exists() || versionCodeLetta < versionCodeCorrente || espansioneVecchia || espansioneCancellata || db.numeroTabelle() < 5 || db.numeroTabelleTesto() < 2);
    }


    public static int isAppInstalled(Context c, String packageName, int minVerCode) {
        try {
            PackageInfo pInfo = c.getPackageManager().getPackageInfo(packageName, 0);
            int verCode = pInfo.versionCode;
            return verCode >= minVerCode ? Costanti.ESPANSIONE_INSTALLATA : Costanti.ESPANSIONE_NON_AGGIORNATA;
        } catch (PackageManager.NameNotFoundException e) {
            return Costanti.ESPANSIONE_MANCANTE;
        }
    }
}