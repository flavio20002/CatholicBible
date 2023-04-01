package com.barisi.flavio.bibbiacattolica;

import android.content.Context;

import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.model.Bibbia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Cache {

    private static List<String> articleIds;
    private static HashMap<String, Bibbia> bibbie;
    private static String[] bibbieEntries;
    private static String[] bibbieEntriesValues;

    public static void clearCache() {
        bibbie = null;
        bibbieEntries = null;
        bibbieEntriesValues = null;
    }

    public static List<String> getArticleIds(Context c) {
        if (articleIds == null) {
            ServiziDatabase db = new ServiziDatabase(c);
            articleIds = db.listaIdArticoli(true);
        }
        return articleIds;
    }

    public static HashMap<String, Bibbia> getBibbie(Context c) {
        if (bibbie == null) {
            ServiziDatabase db = new ServiziDatabase(c);
            bibbie = db.listaBibbie();
        }
        return bibbie;
    }

    public static String[] getBibbieEntries(Context c) {
        if (bibbieEntries == null) {
            ServiziDatabase db = new ServiziDatabase(c);
            bibbieEntries = db.bibbieEntries();
        }
        return bibbieEntries;
    }

    public static String[] getBibbieEntries(Context c, String[] versioniDaEscludere) {
        List<String> items = new ArrayList<>(Arrays.asList(Cache.getBibbieEntries(c)));
        final List<String> itemsCode = new ArrayList<>(Arrays.asList(Cache.getBibbieEntriesValues(c)));
        for (String s : versioniDaEscludere) {
            if (s != null) {
                int posDefault = itemsCode.indexOf(s);
                if (posDefault != -1) {
                    items.remove(posDefault);
                    itemsCode.remove(posDefault);
                }
            }
        }
        return items.toArray(new String[1]);
    }

    public static String[] getBibbieEntriesValues(Context c) {
        if (bibbieEntriesValues == null) {
            ServiziDatabase db = new ServiziDatabase(c);
            bibbieEntriesValues = db.bibbiaEntriesValues();
        }
        return bibbieEntriesValues;
    }

    public static String[] getBibbieEntriesValues(Context c, String[] versioniDaEscludere) {
        List<String> items = new ArrayList<>(Arrays.asList(Cache.getBibbieEntries(c)));
        final List<String> itemsCode = new ArrayList<>(Arrays.asList(Cache.getBibbieEntriesValues(c)));
        for (String s : versioniDaEscludere) {
            if (s != null) {
                int posDefault = itemsCode.indexOf(s);
                if (posDefault != -1) {
                    items.remove(posDefault);
                    itemsCode.remove(posDefault);
                }
            }
        }
        return itemsCode.toArray(new String[1]);
    }
}
