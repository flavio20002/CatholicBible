package com.barisi.flavio.bibbiacattolica.calendario;

import java.util.HashMap;
import java.util.List;

public abstract class CalendarioDAO {

    public String idLibroDaAbbreviazione(String abbr, String lingua) {
        String query = "select id_libro from abbreviazioni where id_abbreviazione like ? and lingua = ?";
        return (String) query(query, new String[]{abbr, lingua}).get(0).get("id_libro");
    }

    public String abbreviazioneDaCodLibro(String codLibro, String lingua) {
        String query = "select id_abbreviazione from abbreviazioni where id_libro like ? and lingua = ?";
        return (String) query(query, new String[]{codLibro, lingua}).get(0).get("id_abbreviazione");
    }

    public String testoCapitolo(String idCapitolo, String versioneBibbia) throws Exception {
        return queryTesto(idCapitolo, versioneBibbia);
    }

    public List<HashMap> liturgiaFestivaGiorno(String data) {
        String query = "select * from liturgia_festiva_giorni t where t.giorno_anno = ?";
        return query(query, new String[]{data});
    }

    public List<HashMap> liturgiaFestiva(String periodo, Integer numPeriodo) {
        String query = "select * from liturgia_festiva_giorni t where t.periodo = ? and t.num_periodo = ?";
        return query(query, new String[]{periodo, String.valueOf(numPeriodo)});
    }

    public List<HashMap> liturgiaFestivaCodice(Integer id, String anno, String lingua) {
        String query = "select dd.nome, descrizione, t.codice_lettura, t.des_lettura, tipo_lettura, anno\n" +
                "                    from liturgia_festiva t, liturgia_des d, liturgia l, liturgia_festiva_giorni_des dd\n" +
                "                    where giorno = ? and anno like '%'||?||'%'\n" +
                "                    and l.codice = tipo_lettura and l.codice = d.codice and d.lingua = ?\n" +
                "                    and dd.id = giorno\n" +
                "                    and dd.lingua = d.lingua\n" +
                "                    order by l.id";
        return query(query, new String[]{String.valueOf(id), anno, lingua});
    }

    public List<HashMap> liturgiaFerialeGiorno(String data) {
        String query = "select * from liturgia_feriale_giorni t where t.giorno_anno = ?";
        return query(query, new String[]{data});
    }

    public List<HashMap> liturgiaFeriale(String periodo, Integer numPeriodo, Integer giornoSettimana) {
        String query = "select * from liturgia_feriale_giorni t where t.periodo = ? and t.num_periodo = ? and t.giorno_settimana = ?";
        return query(query, new String[]{periodo, String.valueOf(numPeriodo), String.valueOf(giornoSettimana)});
    }

    public List<HashMap> liturgiaFerialeCodice(Integer id, String anno, String lingua) {
        String query = "select dd.nome, descrizione, t.codice_lettura, t.des_lettura, tipo_lettura, anno\n" +
                "                    from liturgia_feriale t, liturgia_des d, liturgia l, liturgia_feriale_giorni_des dd\n" +
                "                    where giorno = ? and anno like '%'||?||'%'\n" +
                "                    and l.codice = tipo_lettura and l.codice = d.codice and d.lingua = ?\n" +
                "                    and dd.id = giorno\n" +
                "                    and dd.lingua = d.lingua\n" +
                "                    order by l.id";
        return query(query, new String[]{String.valueOf(id), anno, lingua});
    }

    public abstract List<HashMap> query(String sql, String[] parametri);

    public abstract String queryTesto(String idCapitolo, String versioneBibbia) throws Exception;
}
