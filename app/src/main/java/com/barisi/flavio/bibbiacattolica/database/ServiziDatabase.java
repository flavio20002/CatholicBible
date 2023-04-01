package com.barisi.flavio.bibbiacattolica.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.barisi.flavio.bibbiacattolica.Cache;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.model.Bibbia;
import com.barisi.flavio.bibbiacattolica.model.CacheRicerca;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;
import com.barisi.flavio.bibbiacattolica.model.CapitoloText;
import com.barisi.flavio.bibbiacattolica.model.Categoria;
import com.barisi.flavio.bibbiacattolica.model.Generico;
import com.barisi.flavio.bibbiacattolica.model.Libro;
import com.barisi.flavio.bibbiacattolica.model.Lingua;
import com.barisi.flavio.bibbiacattolica.model.Mappa;
import com.barisi.flavio.bibbiacattolica.model.Preghiera;
import com.barisi.flavio.bibbiacattolica.model.RisultatiRicerca;
import com.barisi.flavio.bibbiacattolica.model.Segnalibro;
import com.barisi.flavio.bibbiacattolica.model.TestoCapitoloPerConfronto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiziDatabase {

    private static final String queryRicercaParola = "select ids from ricerca where parola like ?";
    private static final String queryRicercaParolaNoAccenti = "select ids from ricerca where ifnull(parola2,parola) like ?";
    private static final String queryRicercaTesto = "select rowid from testi where testo like ? order by rowid";
    private static final String queryCapitoliLibro = "select t.id, d.des_libro, numero_capitolo, dd.titolo, d.id_libro, te.testo_breve from capitoli t, capitoli_des dd, libri l, libri_des d, testi_brevi te where t.cod_libro = ? and l.codice = t.cod_libro and te.id=t.id and te.cod_versione = ? and d.lingua = ?  and d.id_libro = l.codice and dd.id = t.id and dd.lingua = d.lingua and numero_capitolo like ? order by t.numero_capitolo";
    private static final String queryNote = "select testo from note t where t.id = ? and lingua = ?";
    private static final String queryIntroduzioneTesto = "select d.des_libro, testo from introduzioni t, libri_des d where t.cod_libro = ? and d.id_libro = t.cod_libro and d.lingua = ? and d.lingua = t.lingua";
    private static final String queryCapitolo = "select d.des_libro, numero_capitolo, dd.titolo, d.id_libro, te.testo_breve from capitoli t, capitoli_des dd, libri l, libri_des d, testi_brevi te where t.id = ? and l.codice = t.cod_libro and te.id=t.id and te.cod_versione = ? and d.lingua = ? and d.id_libro = l.codice and dd.lingua = d.lingua and dd.id = t.id";
    private static final String queryTestoBreveStandard = "select te.testo_breve from capitoli t, libri l, testi_brevi te where t.rowid =? and l.codice = t.cod_libro and te.id=t.id and te.cod_versione = ?";
    private static final String queryCacheRicerca = "select t.rowid, a.id_abbreviazione, d.des_libro, numero_capitolo, titolo, t.id_libro, t.id from capitoli t, capitoli_des dd, libri l, libri_des d, abbreviazioni a where l.codice = t.cod_libro and a.id_libro = l.codice and a.lingua = ? and a.lingua = d.lingua and d.id_libro = l.codice and dd.id = t.id and d.lingua = dd.lingua";
    private static final String queryTestoBreveRicerca = "select substr(testo,case when instr(lower(testo),?)-250 <0 then 1 else instr(lower(testo),?)-250 end,600) from testi where rowid = ?";
    private static final String queryTestoBreveRicercaIgnoraAcc = "select substr(testo,case when instr(replace(replace(replace(replace(replace(replace(replace(lower(testo),'à','a'),'ì','i'),'è','e'),'é','e'),'ò','o'),'ó','o'),'ù','u'),?)-250 <0 then 1 else instr(replace(replace(replace(replace(replace(replace(replace(lower(testo),'à','a'),'ì','i'),'è','e'),'é','e'),'ò','o'),'ó','o'),'ù','u'),?)-250 end,600) from testi where rowid = ?";
    private static final String queryCapitoloTesto = "select testo from testi where testi.id =?";
    private static final String queryIdCapitoli = "select codice from id_capitoli order by id";
    private static final String queryIdCapitoliNoIntro = "select codice from id_capitoli where codice not like '%\\_0' escape '\\' order by id";
    private static final String queryNomeLibroDaCodLibro = "select des_libro from libri_des where id_libro = ? and lingua = ?";
    private static final String queryGuidaAbbreviazioni = "select group_concat(d.des_libro || ' - <b>' || a.id_abbreviazione || '</b>','<br/>') from libri t, libri_des d, abbreviazioni a where t.codice = a.id_libro and d.lingua = a.lingua and a.lingua = ? and d.id_libro = t.codice order by t.id";
    private static final String queryCategorie = "select id, descrizione from categorie where cod_testamento = ? and lingua = ?";
    private static final String queryCapitoliCat = "select l.codice,d.des_libro, l.numero_capitoli,a.id_abbreviazione from libri l, libri_des d, abbreviazioni a where l.codice = d.id_libro and a.id_libro=l.codice and a.lingua=d.lingua and l.id_categoria=? and d.lingua = ? and d.des_libro like ?";
    private static final String queryLibroVaiA = "select d.id_libro,d.des_libro from libri t, libri_des d where t.cod_testamento = ? and d.id_libro = t.codice and d.lingua = ? order by t.id";
    private static final String queryCapitolVaiA = "select id,numero_capitolo from capitoli t where cod_libro = ?";

    private Context context;

    public ServiziDatabase(Context c) {
        this.context = c;
    }

    public Capitolo capitoloCasuale() throws Exception {
        String testamento = Preferenze.ottieniCapitoloCasuale(context);
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("select c.id from capitoli c, libri l where c.cod_libro = l.codice and l.cod_testamento like ? order BY random() limit 1", new String[]{testamento});
            cursor.moveToFirst();
            String id = cursor.getString(0);
            return capitolo(id);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    public List<Segnalibro> listaCapitoliPreferiti() throws Exception {
        PrefReaderHelper myDbHelper = new PrefReaderHelper(context);
        String lingua = Preferenze.ottieniLingua(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        String query = "select " + VersettiPreferitiEntry.COLUMN_NAME_ID_CAPITOLO + "," + VersettiPreferitiEntry.COLUMN_NAME_VERSETTO + "," + VersettiPreferitiEntry.COLUMN_NAME_NOTA +
                " from " + VersettiPreferitiEntry.TABLE_NAME + " order by rowid desc";
        try {
            cursor = database.rawQuery(query, new String[]{});
            ArrayList<Segnalibro> versetti = new ArrayList<>();
            boolean continua = cursor.moveToFirst();
            while (continua) {
                Segnalibro segn = new Segnalibro();
                String idCapitolo = cursor.getString(0);
                String cod_libro = nomeLibroDaCodLibro(idCapitolo.substring(0, idCapitolo.indexOf("_")), lingua);
                String capitolo = idCapitolo.substring(idCapitolo.indexOf("_") + 1);
                String versetto = cursor.getString(1);
                String primoVersetto = versetto;
                if (primoVersetto.contains("-")) {
                    primoVersetto = versetto.substring(0, versetto.indexOf("-"));
                }
                segn.setIdCapitolo(idCapitolo);
                segn.setVersetto(versetto);
                if (!versetto.equals("")) {
                    segn.setRiferimento(cod_libro + " " + capitolo + "," + versetto);
                    segn.setTestoBreve(Regex.versetto(testoCapitolo(idCapitolo).getTesto(), primoVersetto));
                } else {
                    segn.setRiferimento(cod_libro + " " + capitolo);
                    segn.setTestoBreve(capitolo(idCapitolo).getTestoBreve());
                }
                segn.setNota(cursor.getString(2));
                versetti.add(segn);
                continua = cursor.moveToNext();
            }
            return versetti;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    public boolean sePreferito(String id) {
        PrefReaderHelper myDbHelper = new PrefReaderHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("select count(*) from " + VersettiPreferitiEntry.TABLE_NAME + " t where t." + VersettiPreferitiEntry.COLUMN_NAME_ID_CAPITOLO + " = ? and " + VersettiPreferitiEntry.COLUMN_NAME_VERSETTO + " =''", new String[]{String.valueOf(id)});
            cursor.moveToFirst();
            long count = cursor.getLong(0);
            return count > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    public void salvarePreferito(String id, String nota) {
        salvaVersettoPreferito(id, "", nota);
    }

    public void cancellarePreferito(String id) {
        cancellareVersettoPreferito(id, "");
    }

    public List<Capitolo> listaCapitoli(String libro, String query) throws Exception {
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        String versioneBibbia = Preferenze.ottieniVersioneBibbia(context);
        String lingua = Preferenze.ottieniLingua(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        List<Capitolo> capitolos = new ArrayList<>();
        Cursor cursor = null;
        try {
            if (query.equals("")) {
                Capitolo c = new Capitolo();
                c.setId(libro + "_0");
                c.setNomeLibro("");
                c.setNumero(0);
                c.setIdLibro(0);
                capitolos.add(c);
            }
            cursor = database.rawQuery(queryCapitoliLibro, new String[]{libro, versioneBibbia, lingua, query + "%"});
            boolean fine = false;
            cursor.moveToFirst();
            while (!fine) {
                Capitolo capitolo = new Capitolo();
                capitolo.setId(cursor.getString(0));
                capitolo.setNomeLibro(cursor.getString(1));
                capitolo.setNumero(cursor.getInt(2));
                capitolo.setTitolo(cursor.getString(3));
                capitolo.setIdLibro(cursor.getInt(4));
                capitolo.setTestoBreve(Utility.rimuoviAccentiTestoGreco(context, cursor.getString(5), versioneBibbia));
                capitolos.add(capitolo);
                fine = !cursor.moveToNext();
            }
            return capitolos;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    private HashSet<Integer> cercaIdParola(String parolaDaCercare, boolean ignoraAccenti) throws Exception {
        SQLiteOpenHelper myDbHelper = new DatabaseHelper(context, Preferenze.ottieniVersioneBibbia(context));
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(ignoraAccenti ? queryRicercaParolaNoAccenti : queryRicercaParola, new String[]{parolaDaCercare});
            List<Integer> capId = new ArrayList<>();
            boolean fine = false;
            if (cursor.moveToFirst()) {
                while (!fine) {
                    String ids = cursor.getString(0);
                    String[] res = TextUtils.split(ids, ";");
                    for (String re : res) {
                        capId.add(Integer.valueOf(re));
                    }
                    fine = !cursor.moveToNext();
                }
            }
            return new HashSet<>(capId);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    private List<Integer> cercaIdTesto(String testoDaCercare) throws Exception {
        SQLiteOpenHelper myDbHelper = new DatabaseHelper(context, Preferenze.ottieniVersioneBibbia(context));
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(queryRicercaTesto, new String[]{testoDaCercare});
            List<Integer> capId = new ArrayList<>();
            boolean fine = false;
            if (cursor.moveToFirst()) {
                while (!fine) {
                    Integer id = cursor.getInt(0);
                    capId.add(id);
                    fine = !cursor.moveToNext();
                }
            }
            return capId;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    private List<Integer> cercaIdParole(List<String> paroleDaCercare, boolean ignoraAccenti) throws Exception {
        if (paroleDaCercare.size() == 0) {
            return new ArrayList<>();
        } else {
            Set<Integer> s2 = new HashSet<>();
            for (int i = 0; i < paroleDaCercare.size(); i++) {
                Set<Integer> s1 = cercaIdParola(paroleDaCercare.get(i), ignoraAccenti);
                if (s2.size() == 0) {
                    s2 = s1;
                } else {
                    s2.retainAll(s1);
                }
            }
            ArrayList<Integer> result = new ArrayList<>(s2);
            Collections.sort(result);
            return result;
        }
    }

    public RisultatiRicerca cercaCapitoli(String filtro, boolean parolaEsatta, boolean ignoraAccenti) throws Exception {
        String f = ignoraAccenti ? Regex.rimuoviAccenti(filtro) : filtro;
        RisultatiRicerca result = new RisultatiRicerca();
        List<Integer> capitoli;
        List<String> paroleDaCercare = null;
        if (f.startsWith("\"")) {
            capitoli = cercaIdTesto("%" + f.replace("\"", "").toLowerCase() + "%");
        } else {
            paroleDaCercare = Regex.paroleRicerca(f.trim().toLowerCase(), parolaEsatta);
            capitoli = cercaIdParole(paroleDaCercare, ignoraAccenti);
        }
        result.setRowIds(capitoli);
        result.setFiltro(filtro(f.trim().toLowerCase(), paroleDaCercare, parolaEsatta, ignoraAccenti));
        return result;
    }

    private String filtro(String f, List<String> parole, boolean parolaEsatta, boolean ignoraAccenti) throws Exception {
        String filtro = "";
        if (f.startsWith("\"")) {
            filtro = f.replace("\"", "").trim();
            return "\"\\w*" + filtro + "\\w*\"";
        } else {
            for (int i = 0; i < parole.size(); i++) {
                if (!parolaEsatta) {
                    filtro += parole.get(i) + "\\w* ";
                } else {
                    filtro += parole.get(i) + " ";
                }
            }
            filtro = filtro.replace("%", "").trim();
            if (ignoraAccenti) {
                return "!" + filtro;
            } else {
                return filtro;
            }
        }
    }

    public Capitolo capitolo(String idCapitolo) throws Exception {
        ArrayList<String> cap = new ArrayList<>();
        cap.add(idCapitolo);
        return capitoli(cap).get(0);
    }

    public String testoBreveStandard(Integer rowid) {
        String result = null;
        String versioneBibbia = Preferenze.ottieniVersioneBibbia(context);
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        try {
            Cursor cursor = null;
            try {
                cursor = database.rawQuery(queryTestoBreveStandard, new String[]{String.valueOf(rowid), versioneBibbia});
                cursor.moveToFirst();
                result = Utility.rimuoviAccentiTestoGreco(context, cursor.getString(0), versioneBibbia);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e("ServiziDatabase", e.getMessage());
            e.printStackTrace();
        }
        myDbHelper.close();
        return result;
    }

    /*public Capitolo capitoloDaRowid(Integer idCapitolo) throws Exception {
        Capitolo capitolo = new Capitolo();
        String versioneBibbia = Preferenze.ottieniVersioneBibbia(context);
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        try {
            Cursor cursor = null;
            try {
                cursor = database.rawQuery(queryCapitolo2, new String[]{String.valueOf(idCapitolo), versioneBibbia});
                cursor.moveToFirst();
                capitolo.setId(cursor.getString(0));
                capitolo.setNomeLibro(cursor.getString(1));
                capitolo.setNumero(cursor.getInt(2));
                capitolo.setTitolo(cursor.getString(3));
                capitolo.setIdLibro(cursor.getInt(4));
                capitolo.setTestoBreve(Utility.rimuoviAccentiTestoGreco(cursor.getString(5), versioneBibbia));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e("ServiziDatabase", e.getMessage());
        }
        myDbHelper.close();
        return capitolo;
    }/*

    /*public String siglaLibroDaRowid(Integer idCapitolo) throws Exception {
        String result = "";
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        String lingua = Preferenze.ottieniLingua(context);
        try {
            Cursor cursor = null;
            try {
                cursor = database.rawQuery(querySiglaCapitoloDaRowId, new String[]{String.valueOf(idCapitolo), lingua});
                cursor.moveToFirst();
                result = cursor.getString(0);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e("ServiziDatabase", e.getMessage());
        }
        myDbHelper.close();
        return result;
    }*/

    public SparseArray<CacheRicerca> cacheRicerca() {
        String lingua = Preferenze.ottieniLingua(context);
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(queryCacheRicerca, new String[]{lingua});
            SparseArray<CacheRicerca> risultato = new SparseArray<>();
            boolean continua = cursor.moveToFirst();
            while (continua) {
                CacheRicerca c = new CacheRicerca();
                c.setRowId(cursor.getInt(0));
                c.setSiglaCapitolo(cursor.getString(1));
                c.setNomeLibro(cursor.getString(2));
                c.setNumero(cursor.getInt(3));
                c.setTitolo(cursor.getString(4));
                c.setIdLibro(cursor.getInt(5));
                c.setId(cursor.getString(6));
                risultato.put(c.getRowId(), c);
                continua = cursor.moveToNext();
            }
            return risultato;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    /*public List<Capitolo> capitoliDaRowid(List<Integer> cap) throws Exception {
        List<Capitolo> capitolos = new ArrayList<>();
        String versioneBibbia = Preferenze.ottieniVersioneBibbia(context);
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        for (int i = 0; i < cap.size(); i++) {
            try {
                Cursor cursor = null;
                try {
                    Integer idCapitolo = cap.get(i);
                    cursor = database.rawQuery(queryCapitolo2, new String[]{String.valueOf(idCapitolo), versioneBibbia});
                    cursor.moveToFirst();
                    Capitolo capitolo = new Capitolo();
                    capitolo.setId(cursor.getString(0));
                    capitolo.setNomeLibro(cursor.getString(1));
                    capitolo.setNumero(cursor.getInt(2));
                    capitolo.setTitolo(cursor.getString(3));
                    capitolo.setIdLibro(cursor.getInt(4));
                    capitolo.setTestoBreve(Utility.rimuoviAccentiTestoGreco(cursor.getString(5), versioneBibbia));
                    capitolos.add(capitolo);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } catch (Exception e) {
                Log.e("ServiziDatabase", e.getMessage());
            }
        }
        myDbHelper.close();
        return capitolos;
    }*/

    public List<Capitolo> capitoli(List<String> cap) throws Exception {
        List<Capitolo> capitolos = new ArrayList<>();
        String versioneBibbia = Preferenze.ottieniVersioneBibbia(context);
        String lingua = Preferenze.ottieniLingua(context);
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        for (int i = 0; i < cap.size(); i++) {
            try {
                String idCapitolo = cap.get(i);
                if (idCapitolo.endsWith("_0")) {
                    Capitolo capitolo = new Capitolo();
                    capitolo.setId(idCapitolo);
                    capitolo.setNomeLibro("");
                    capitolo.setNumero(0);
                    capitolos.add(capitolo);
                } else {
                    Cursor cursor = null;
                    try {
                        cursor = database.rawQuery(queryCapitolo, new String[]{idCapitolo, versioneBibbia, lingua});
                        cursor.moveToFirst();
                        Capitolo capitolo = new Capitolo();
                        capitolo.setId(idCapitolo);
                        capitolo.setNomeLibro(cursor.getString(0));
                        capitolo.setNumero(cursor.getInt(1));
                        capitolo.setTitolo(cursor.getString(2));
                        capitolo.setIdLibro(cursor.getInt(3));
                        capitolo.setTestoBreve(Utility.rimuoviAccentiTestoGreco(context, cursor.getString(4), versioneBibbia));
                        capitolos.add(capitolo);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ServiziDatabase", e.getMessage());
                e.printStackTrace();
            }
        }
        myDbHelper.close();
        return capitolos;
    }

    public String testoBreveCapitoloRicerca(int rowid, String parola) throws Exception {
        List<String> versettiRisultato = new ArrayList<>();
        String versioneBibbia = Preferenze.ottieniVersioneBibbia(context);
        int coloreSpeciale = Preferenze.coloreSpeciale(context);
        String сolorString = String.format("%X", coloreSpeciale).substring(2);
        String sostituzione = String.format("<b><font color='#%s'>$1</font></b>", сolorString);
        DatabaseHelper myDbHelper2 = new DatabaseHelper(context, versioneBibbia);
        SQLiteDatabase database2 = myDbHelper2.getReadableDatabase();
        Cursor cursor2 = null;
        String filtroParola;
        boolean ignoraAccenti = false;
        try {
            if (parola.startsWith("\"")) {
                filtroParola = parola.replace("\"", "").replace("\\w*", "");
            } else if (parola.startsWith("!")) {
                ignoraAccenti = true;
                String[] parole = parola.substring(1).split("\\s+");
                filtroParola = parole.length > 0 ? parole[0] : parola;
                filtroParola = filtroParola.replace("\\w*", "");
            } else {
                String[] parole = parola.split("\\s+");
                filtroParola = parole.length > 0 ? parole[0] : parola;
                filtroParola = filtroParola.replace("\\w*", "");
            }
            cursor2 = database2.rawQuery(ignoraAccenti ? queryTestoBreveRicercaIgnoraAcc : queryTestoBreveRicerca, new String[]{filtroParola, filtroParola, String.valueOf(rowid)});
            cursor2.moveToFirst();
            String testo = cursor2.getString(0);
            HashMap<String, String> versetti = Regex.versettiPerConfronto2(Utility.rimuoviAccentiTestoGreco(context, testo, versioneBibbia));
            List<String> numeri = new ArrayList<>(versetti.values());
            numeri.remove(numeri.size() - 1);
            for (String v : numeri) {
                if (parola.startsWith("\"")) {
                    String p = parola.replaceAll("[.,!?\"<>]", "");
                    if (!p.equals("\\w*\\w*")) {
                        versettiRisultato.add(v.replaceAll("(?i)\\b(?<!<)(" + p + ")(?!>)(?!=)(?!\">)\\b", sostituzione));
                    }
                } else if (parola.startsWith("!")) {
                    String[] paroleTemp = parola.substring(1).split("\\s+");
                    List<String> parole = Regex.listaParoleIgnoraAccenti(paroleTemp, testo);
                    String vModificato = v;
                    for (String aParole : parole) {
                        vModificato = vModificato.replaceAll("(?i)\\b(?<!<)(" + aParole + ")(?!>)(?!=)(?!\">)\\b", sostituzione);
                    }
                    versettiRisultato.add(vModificato);
                } else {
                    String[] parole = parola.split("\\s+");
                    String vModificato = v;
                    for (String aParole : parole) {
                        vModificato = vModificato.replaceAll("(?i)\\b(?<!<)(" + aParole + ")(?!>)(?!=)(?!\">)\\b", sostituzione);
                    }
                    versettiRisultato.add(vModificato);
                }
            }
            String risultato = "";
            for (String v : versettiRisultato) {
                if (risultato.equals("") && v.contains("<b>")) {
                    risultato = v;
                } else if (!risultato.equals("")) {
                    risultato += " " + v;
                }
            }
            return Utility.rimuoviAccentiTestoGreco(context, risultato, versioneBibbia);
        } finally {
            if (cursor2 != null) {
                cursor2.close();
            }
            myDbHelper2.close();
        }
    }

    public CapitoloText testoCapitolo(String idCapitolo, String versioneBibbia) throws Exception {
        String lingua = Preferenze.ottieniLingua(context);
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        DatabaseHelper myDbHelper2 = new DatabaseHelper(context, versioneBibbia);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        SQLiteDatabase database2 = myDbHelper2.getReadableDatabase();
        Cursor cursor = null;
        Cursor cursor2 = null;
        CapitoloText capitoloText;
        try {
            if (idCapitolo.endsWith("_0")) {
                String cod_libro = idCapitolo.substring(0, idCapitolo.indexOf("_"));
                cursor = database.rawQuery(queryIntroduzioneTesto, new String[]{cod_libro, lingua});
                cursor.moveToFirst();
                capitoloText = new CapitoloText();
                capitoloText.setNomeLibro(cursor.getString(0));
                capitoloText.setTesto(cursor.getString(1));
                capitoloText.setNumero(0);
            } else {
                cursor = database.rawQuery(queryCapitolo, new String[]{idCapitolo, versioneBibbia, lingua});
                cursor2 = database2.rawQuery(queryCapitoloTesto, new String[]{idCapitolo});
                cursor.moveToFirst();
                cursor2.moveToFirst();
                capitoloText = new CapitoloText();
                capitoloText.setId(idCapitolo);
                capitoloText.setNomeLibro(cursor.getString(0));
                capitoloText.setNumero(cursor.getInt(1));
                capitoloText.setTitolo(cursor.getString(2));
                String testo;
                if (cursor2.getType(0) == Cursor.FIELD_TYPE_BLOB) {
                    testo = Utility.decompress(cursor2.getBlob(0));
                } else {
                    testo = cursor2.getString(0);
                }
                capitoloText.setTesto(Utility.rimuoviAccentiTestoGreco(context, testo, versioneBibbia));
                return capitoloText;
            }
            return capitoloText;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (cursor2 != null) {
                cursor2.close();
            }

            myDbHelper.close();
            myDbHelper2.close();
        }
    }

    private CapitoloText testoCapitolo(String idCapitolo) throws Exception {
        String versioneBibbia = Preferenze.ottieniVersioneBibbia(context);
        return testoCapitolo(idCapitolo, versioneBibbia);
    }

    public TestoCapitoloPerConfronto testoCapitoloPerConfronto(String idCapitolo, String versioneSinistra, String versioneDestra, String terzaVersione) throws Exception {
        CapitoloText testo1 = testoCapitolo(idCapitolo, versioneSinistra); //Con bibbia predefinita
        CapitoloText testo2 = testoCapitolo(idCapitolo, versioneDestra); //Con bibbia di confronto
        String text1 = testo1.getTesto();
        String text2 = testo2.getTesto();
        TestoCapitoloPerConfronto capitoloText = new TestoCapitoloPerConfronto();
        capitoloText.setNomeLibro(testo1.getNomeLibro());
        capitoloText.setNumero(testo1.getNumero());
        capitoloText.setTesto1(Regex.versettiPerConfronto(text1));
        capitoloText.setTesto2(Regex.versettiPerConfronto(text2));
        capitoloText.setDirezionePrima(versioneSinistra.equals("ebraico") ? "testoebraico" : "testoLatino");
        capitoloText.setDirezioneSeconda(versioneDestra.equals("ebraico") ? "testoebraico" : "testoLatino");
        List<String> items = new ArrayList<>(Arrays.asList(Cache.getBibbieEntries(context)));
        final List<String> itemsCode = new ArrayList<>(Arrays.asList(Cache.getBibbieEntriesValues(context)));
        capitoloText.setTitoloPrimabibbia(items.get(itemsCode.indexOf(versioneSinistra)));
        capitoloText.setTitoloSecondabibbia(items.get(itemsCode.indexOf(versioneDestra)));
        if (terzaVersione != null) {
            CapitoloText testo3 = testoCapitolo(idCapitolo, terzaVersione);
            String text3 = testo3.getTesto();
            capitoloText.setTesto3(Regex.versettiPerConfronto(text3));
            capitoloText.setTitoloTerzabibbia(items.get(itemsCode.indexOf(terzaVersione)));
            capitoloText.setDirezioneTerza(terzaVersione.equals("ebraico") ? "testoebraico" : "testoLatino");
        }
        return capitoloText;
    }

    public List<Categoria> listaLibri(String codTestamento, String query) {
        String lingua = Preferenze.ottieniLingua(context);
        List<Categoria> result = new ArrayList<>();
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(queryCategorie, new String[]{codTestamento, lingua});
            while (cursor.moveToNext()) {
                Categoria sup = new Categoria();
                sup.setIdCategoria(cursor.getLong(0));
                sup.setDesGategoria(cursor.getString(1));
                result.add(sup);
                Cursor cursor2 = null;
                try {
                    cursor2 = database.rawQuery(queryCapitoliCat, new String[]{String.valueOf(sup.getIdCategoria()), lingua, "%" + query + "%"});
                    while (cursor2.moveToNext()) {
                        Libro libro = new Libro();
                        libro.setCodLibro(cursor2.getString(0));
                        libro.setDesLibro(cursor2.getString(1));
                        libro.setNumeroCapitoli(cursor2.getInt(2));
                        libro.setAbbreviazione(cursor2.getString(3));
                        sup.addLibro(libro);
                    }
                } finally {
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                }
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    public List<String> listaIdArticoli(boolean introduzione) {
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            if (introduzione) {
                cursor = database.rawQuery(queryIdCapitoli, new String[]{});
            } else {
                cursor = database.rawQuery(queryIdCapitoliNoIntro, new String[]{});
            }
            ArrayList<String> capitolos = new ArrayList<>();
            boolean continua = cursor.moveToFirst();
            while (continua) {
                capitolos.add(cursor.getString(0));
                continua = cursor.moveToNext();
            }
            return capitolos;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    public String note(String idCapitolo) throws Exception {
        String lingua = Preferenze.ottieniLingua(context);
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(queryNote, new String[]{idCapitolo, lingua});
            cursor.moveToFirst();
            return cursor.getString(0);
        } catch (Exception e) {
            Log.e("note", e.getMessage());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    /*private String abbreviazioneDaCodLibro(String codLibro, String lingua) {
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(queryAbbreviazioneDaCodLibro, new String[]{codLibro, lingua});
            cursor.moveToFirst();
            return cursor.getString(0);
        } catch (Exception e) {
            Log.e("abbreviazioneDaCodLibro", e.getMessage());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }*/

    private String nomeLibroDaCodLibro(String codLibro, String lingua) {
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(queryNomeLibroDaCodLibro, new String[]{codLibro, lingua});
            cursor.moveToFirst();
            return cursor.getString(0);
        } catch (Exception e) {
            Log.e("abbreviazioneDaCodLibro", e.getMessage());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    public String guidaAbbreviazioni(String lingua) {
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(queryGuidaAbbreviazioni, new String[]{lingua});
            cursor.moveToFirst();
            return cursor.getString(0);
        } catch (Exception e) {
            Log.e("guidaAbbreviazioni", e.getMessage());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    public void salvaVersettoPreferito(String idCapitolo, String versetto, String nota) {
        PrefReaderHelper myDbHelper = new PrefReaderHelper(context);
        SQLiteDatabase db = null;
        try {
            db = myDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VersettiPreferitiEntry.COLUMN_NAME_ID_CAPITOLO, idCapitolo);
            values.put(VersettiPreferitiEntry.COLUMN_NAME_VERSETTO, versetto);
            values.put(VersettiPreferitiEntry.COLUMN_NAME_NOTA, nota);
            db.insert(VersettiPreferitiEntry.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e("guidaAbbreviazioni", e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
            myDbHelper.close();
        }

    }


    public void cancellareVersettoPreferito(String idCapitolo, String versetto) {
        PrefReaderHelper myDbHelper = new PrefReaderHelper(context);
        SQLiteDatabase database = myDbHelper.getWritableDatabase();
        String selection = VersettiPreferitiEntry.COLUMN_NAME_ID_CAPITOLO + " = ? AND " + VersettiPreferitiEntry.COLUMN_NAME_VERSETTO + " =?";
        String[] selectionArgs = {idCapitolo, versetto};
        database.delete(VersettiPreferitiEntry.TABLE_NAME, selection, selectionArgs);
        myDbHelper.close();
    }

    public void cancellareVersettiPreferiti() {
        PrefReaderHelper myDbHelper = new PrefReaderHelper(context);
        SQLiteDatabase database = myDbHelper.getWritableDatabase();
        database.delete(VersettiPreferitiEntry.TABLE_NAME, null, null);
        myDbHelper.close();
    }

    public List<String> listaVersettiPreferitiPerCapitolo(String idCapitolo) {
        PrefReaderHelper myDbHelper = new PrefReaderHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        String query = "select " + VersettiPreferitiEntry.COLUMN_NAME_VERSETTO + " from " + VersettiPreferitiEntry.TABLE_NAME + " where " + VersettiPreferitiEntry.COLUMN_NAME_ID_CAPITOLO + " =?";
        try {
            cursor = database.rawQuery(query, new String[]{idCapitolo});
            ArrayList<String> versetti = new ArrayList<>();
            boolean continua = cursor.moveToFirst();
            while (continua) {
                versetti.add(cursor.getString(0));
                continua = cursor.moveToNext();
            }
            return versetti;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    public void aggiornareNotaVersettoPreferito(String idCapitolo, String versetto, String nota) {
        PrefReaderHelper myDbHelper = new PrefReaderHelper(context);
        try {
            SQLiteDatabase db = myDbHelper.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(VersettiPreferitiEntry.COLUMN_NAME_NOTA, nota);
            String selection = VersettiPreferitiEntry.COLUMN_NAME_ID_CAPITOLO + " = ? AND " + VersettiPreferitiEntry.COLUMN_NAME_VERSETTO + " =?";
            String[] selectionArgs = {idCapitolo, versetto};
            db.update(VersettiPreferitiEntry.TABLE_NAME, cv, selection, selectionArgs);
        } finally {
            myDbHelper.close();
        }
    }

    public List<HashMap> query(String sql, String[] parametri) {
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            ArrayList<HashMap> result = new ArrayList<>();
            cursor = database.rawQuery(sql, parametri);
            boolean continua = cursor.moveToFirst();
            while (continua) {
                HashMap<String, Object> map = new HashMap<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (cursor.getType(i) == Cursor.FIELD_TYPE_INTEGER) {
                        map.put(cursor.getColumnName(i), cursor.getInt(i));
                    } else {
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                }
                result.add(map);
                continua = cursor.moveToNext();
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    int numeroTabelle() {
        String q = "SELECT count(*)as num FROM sqlite_master where type = 'table'";
        return (int) query(q, new String[]{}).get(0).get("num");
    }

    int numeroTabelleTesto() {
        Cursor cursor = null;
        SQLiteOpenHelper myDbHelper = null;
        try {
            myDbHelper = new DatabaseHelper(context, Preferenze.ottieniVersioneBibbia(context));
            SQLiteDatabase database = myDbHelper.getReadableDatabase();
            String q = "SELECT count(*)as num FROM sqlite_master where type = 'table'";
            cursor = database.rawQuery(q, new String[]{});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (myDbHelper != null) {
                myDbHelper.close();
            }
        }
        return 0;
    }

    public List<Generico> listaLibriVaiA(String codTestamento) {
        String lingua = Preferenze.ottieniLingua(context);
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(queryLibroVaiA, new String[]{codTestamento, lingua});
            ArrayList<Generico> libri = new ArrayList<>();
            boolean continua = cursor.moveToFirst();
            while (continua) {
                libri.add(new Generico(cursor.getString(0), cursor.getString(1)));
                continua = cursor.moveToNext();
            }
            return libri;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    public List<Generico> listaCapitoliVaiA(String codLibro) {
        DatabaseHelper myDbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = myDbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(queryCapitolVaiA, new String[]{codLibro});
            ArrayList<Generico> libri = new ArrayList<>();
            boolean continua = cursor.moveToFirst();
            while (continua) {
                libri.add(new Generico(cursor.getString(0), cursor.getString(1)));
                continua = cursor.moveToNext();
            }
            return libri;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            myDbHelper.close();
        }
    }

    public HashMap<String, Bibbia> listaBibbie() {
        HashMap<String, Bibbia> result = new HashMap<>();
        List<HashMap> bib = query("select * from bibbie", new String[]{});
        for (int i = 0; i < bib.size(); i++) {
            Bibbia p = new Bibbia((String) bib.get(i).get("file"), (String) bib.get(i).get("add-on"), (Integer) bib.get(i).get("min_versione_add-on"), (String) bib.get(i).get("linguaBibbia"));
            result.put((String) bib.get(i).get("cod"), p);
        }
        return result;
    }

    public String[] bibbieEntries() {
        String lingua = Preferenze.ottieniLingua(context);
        List<String> result = new ArrayList<>();
        List<HashMap> bib = query("select d.des from bibbie_des d,bibbie b where d.cod=b.cod and lingua = ? order by b.id", new String[]{lingua});
        for (int i = 0; i < bib.size(); i++) {
            result.add((String) bib.get(i).get("des"));
        }
        return result.toArray(new String[]{});
    }

    public String[] bibbiaEntriesValues() {
        List<String> result = new ArrayList<>();
        List<HashMap> bib = query("select cod from bibbie order by id", new String[]{});
        for (int i = 0; i < bib.size(); i++) {
            result.add((String) bib.get(i).get("cod"));
        }
        return result.toArray(new String[]{});
    }

    public List<Mappa> listaMappe() {
        String lingua = Preferenze.ottieniLingua(context);
        List<Mappa> result = new ArrayList<>();
        List<HashMap> mappe = query("select m.immagine,d.descrizione, d.des_breve, d.anno from mappe m, mappe_des d where m.id=d.id and m.lingua=d.lingua and d.lingua=? order by m.anno asc", new String[]{lingua});
        for (HashMap h : mappe) {
            Mappa m = new Mappa();
            m.setNomeMappa((String) h.get("descrizione"));
            m.setNomeBreveMappa((String) h.get("des_breve"));
            m.setAnno((String) h.get("anno"));
            m.setImmagine(context.getResources().getIdentifier((String) h.get("immagine"), "drawable", context.getPackageName()));
            result.add(m);
        }
        return result;
    }

    public List<Preghiera> listaPreghiere(String query) {
        String lingua = Preferenze.ottieniLingua(context);
        List<Preghiera> result = new ArrayList<>();
        List<HashMap> mappe = query("select id, nome from preghiere where lingua=? and nome like ? order by nome asc", new String[]{lingua, "%" + query + "%"});
        for (HashMap h : mappe) {
            Preghiera p = new Preghiera();
            p.setId((Integer) h.get("id"));
            p.setNomepreghiera((String) h.get("nome"));
            result.add(p);
        }
        return result;
    }

    public String testoPreghiera(int id, String lingua) {
        List<HashMap> mappe = query("select testo from preghiere_testi where id=? and lingua=?", new String[]{String.valueOf(id), lingua});
        return (String) mappe.get(0).get("testo");
    }

    public List<Lingua> listaLinguePreghiera() {
        String lingua = Preferenze.ottieniLingua(context);
        List<Lingua> result = new ArrayList<>();
        List<HashMap> mappe = query("select cod_lingua_preghiera, des_lingua_preghiera from preghiere_lingue where lingua=?", new String[]{lingua});
        for (HashMap h : mappe) {
            Lingua p = new Lingua();
            p.setCod((String) h.get("cod_lingua_preghiera"));
            p.setDes((String) h.get("des_lingua_preghiera"));
            result.add(p);
        }
        return result;
    }
}