package com.barisi.flavio.bibbiacattolica.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.barisi.flavio.bibbiacattolica.Cache;
import com.barisi.flavio.bibbiacattolica.model.Bibbia;

import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME_DEFAULT = "database.db";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME_DEFAULT, null, 1);
    }

    DatabaseHelper(Context context, String versioneBibbia) throws Exception {
        super(context, databaseName(context, versioneBibbia), null, 1);
    }

    DatabaseHelper(Context context, String nomeFile, int dummy) throws Exception {
        super(context, nomeFile, null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    static String databaseName(Context c, String versioneBibbia) throws Exception {
        HashMap<String, Bibbia> bibbie = Cache.getBibbie(c);
        Bibbia bibbia = bibbie.get(versioneBibbia);
        return bibbia.getFile();
    }

    public static String databaseAddOn(Context c, String versioneBibbia) throws Exception {
        return Cache.getBibbie(c).get(versioneBibbia).getAddOn();
    }

    public static String databaseLanguage(Context c, String versioneBibbia) {
        return Cache.getBibbie(c).get(versioneBibbia).getLinguaBibbia();
    }

    public static int databaseMinVersioneEspansione(Context c, String versioneBibbia) {
        return Cache.getBibbie(c).get(versioneBibbia).getMinimaversioneAddOn();
    }
}