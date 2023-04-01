
package com.barisi.flavio.bibbiacattolica.calendario;

import android.content.Context;

import com.barisi.flavio.bibbiacattolica.Cache;
import com.barisi.flavio.bibbiacattolica.Html;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.gui.Css;
import com.barisi.flavio.bibbiacattolica.model.CapitoloLetture;
import com.barisi.flavio.bibbiacattolica.model.Lettura;
import com.barisi.flavio.bibbiacattolica.model.Pair;
import com.barisi.flavio.bibbiacattolica.model.Quadruple;
import com.barisi.flavio.bibbiacattolica.model.TestoCapitoloPerConfronto;
import com.barisi.flavio.bibbiacattolica.model.TestoLettureGiornoConfronto;
import com.barisi.flavio.bibbiacattolica.model.Triple;
import com.barisi.flavio.bibbiacattolica.model.Versetto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@SuppressWarnings("ALL")
public class DataUtils {

    public static String estraiTestoLetturaDaCodice(String codice, boolean mostraVersetti, CalendarioDAO dao, String versioneBibbia, String lingua, String tipoLettura) {
        List<Lettura> letture = estraiLettureDaCodice(codice, false, dao, lingua);
        if (letture != null && letture.size() > 0) {
            return estraiTestoLetturaDaLettura(letture.get(0), mostraVersetti, dao, versioneBibbia, tipoLettura);
        } else {
            return "";
        }
    }

    public static List<Versetto> estraiVersettiLetturaDaCodice(String codice, CalendarioDAO dao, String versioneBibbia, String lingua, String tipoLettura) {
        List<Lettura> letture = estraiLettureDaCodice(codice, false, dao, lingua);
        if (letture != null && letture.size() > 0) {
            return estraiVersettiLetturaDaLettura(letture.get(0), dao, versioneBibbia, tipoLettura);
        } else {
            return new ArrayList<Versetto>();
        }
    }

    public static String estraiDescrizioneLocalizzata(String codice, CalendarioDAO dao, String lingua) {
        List<Lettura> letture = estraiLettureDaCodice(codice, false, dao, lingua);
        if (letture != null && letture.size() > 0) {
            return codice.replace(letture.get(0).getAutore(), dao.abbreviazioneDaCodLibro(letture.get(0).getAutore(), lingua));
        } else {
            return "";
        }
    }

    public static List<Lettura> estraiLettureDaCodice(String codice, boolean codiciLocalizzati, CalendarioDAO dao, String lingua) {
        ArrayList<Lettura> lett = Regex.decodificaLetture(codice);
        for (Lettura l : lett) {
            if (codiciLocalizzati) {
                String abbreviazione = "";
                try {
                    abbreviazione = dao.idLibroDaAbbreviazione(l.getAutore(), lingua);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                l.setAutore(abbreviazione);
            }
        }
        return lett;
    }

    public static List<Versetto> estraiVersettiLetturaDaLettura(Lettura l, CalendarioDAO dao, String versioneBibbia, String tipoLettura) {
        List<Versetto> risultato = new ArrayList<Versetto>();
        try {
            for (int j = 0; j < l.getCapitoloLettura().size(); j++) {
                CapitoloLetture cap = l.getCapitoloLettura().get(j);
                String autore = l.getAutore();
                String testoCap = dao.testoCapitolo(autore + "_" + cap.getCapitolo(), versioneBibbia);
                HashMap<Integer, HashMap<String, Versetto>> tuttiVersetti = Regex.versettiPerLetture(testoCap, true);
                if (cap.getVersetti().size() == 0) {
                    Collection<HashMap<String, Versetto>> vers = tuttiVersetti.values();
                    for (HashMap<String, Versetto> vv : vers) {
                        Versetto v = Regex.concatenaVersetti(vv);
                        String testoVersetto = Html.fromHtml(pulisciSalmo(autore, v.getNumero(), v.getTesto())).toString();
                        Versetto versTemp = new Versetto();
                        versTemp.setNumero(v.getNumero());
                        versTemp.setTesto(testoVersetto);
                        risultato.add(versTemp);
                    }
                } else {
                    for (Quadruple<String, String, String, String> v : cap.getVersetti()) {
                        String ris = "";
                        String start = v.getVal0();
                        String startLettera = v.getVal1();
                        String end = v.getVal2();
                        String endLettera = v.getVal3();
                        HashMap<Integer, Versetto> versetti = Regex.versettiPerLettureInterna(tuttiVersetti, start, startLettera, end, endLettera);
                        Collection<Versetto> vers = versetti.values();
                        for (Versetto ver : vers) {
                            if (ver != null) {
                                String testoVersetto = Html.fromHtml(pulisciSalmo(autore, ver.getNumero(), ver.getTesto())).toString();
                                Versetto versTemp = new Versetto();
                                versTemp.setNumero(ver.getNumero());
                                versTemp.setTesto(testoVersetto);
                                risultato.add(versTemp);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(l.getDescrizione() + " errore: " + e.getMessage());
        }
        return risultato;
    }

    public static String estraiTestoLetturaDaLettura(Lettura l, boolean mostraVersetti, CalendarioDAO dao, String versioneBibbia, String tipoLettura) {
        String risultato = "";
        try {
            for (int j = 0; j < l.getCapitoloLettura().size(); j++) {
                CapitoloLetture cap = l.getCapitoloLettura().get(j);
                String autore = l.getAutore();
                String testoCap = dao.testoCapitolo(autore + "_" + cap.getCapitolo(), versioneBibbia);
                HashMap<Integer, HashMap<String, Versetto>> tuttiVersetti = Regex.versettiPerLetture(testoCap, true);
                if (cap.getVersetti().size() == 0) {
                    Collection<HashMap<String, Versetto>> vers = tuttiVersetti.values();
                    for (HashMap<String, Versetto> vv : vers) {
                        Versetto v = Regex.concatenaVersetti(vv);
                        String testoVersetto = pulisciSalmo(autore, v.getNumero(), v.getTesto());
                        if (mostraVersetti) {
                            risultato += "<sup>" + v.getNumero() + "</sup>" + testoVersetto + " ";
                        } else {
                            risultato += testoVersetto + " ";
                        }
                    }
                } else {
                    for (Quadruple<String, String, String, String> v : cap.getVersetti()) {
                        String ris = "";
                        String start = v.getVal0();
                        String startLettera = v.getVal1();
                        String end = v.getVal2();
                        String endLettera = v.getVal3();
                        HashMap<Integer, Versetto> versetti = Regex.versettiPerLettureInterna(tuttiVersetti, start, startLettera, end, endLettera);
                        Collection<Versetto> vers = versetti.values();
                        for (Versetto ver : vers) {
                            if (ver != null) {
                                String testoVersetto = pulisciSalmo(autore, ver.getNumero(), ver.getTesto());
                                if (mostraVersetti) {
                                    ris += "<sup>" + ver.getNumero() + "</sup>" + testoVersetto + " ";
                                } else {
                                    ris += testoVersetto + " ";
                                }
                            }
                        }
                        if (tipoLettura.startsWith("S")) {
                            risultato += "<p>" + Util.capitalizza(ris.replaceFirst("^\\s+", "")) + "</p>";
                        } else {
                            risultato += ris;
                        }
                    }
                    if (risultato.endsWith("<br> <br>")) {
                        risultato = risultato.substring(0, risultato.length() - 5);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(l.getDescrizione() + " errore: " + e.getMessage());
        }
        return Util.capitalizza(risultato).trim();
    }

    private static String pulisciSalmo(String autore, String numero, String testo) {
        if (autore.equals("Sal") && testo != null) {
            String result = testo.replaceAll("^\\(\\d+,\\d+\\) ", "");
            if (numero.matches("1[^\\d]?.*")) {
                result = result.replaceAll(".*Di Davide\\.<br>", "")
                        .replace("Canto delle salite.<br>", "")
                        .replace("Maskil. Di Davide.", "")
                        .replaceAll(".*Di Davide\\. Salmo\\.<br>", "")
                        .replace("Salmo.<br>", "")
                        .replace("Salmo.", "")
                        .replaceAll(".*Di Asaf\\.<br>", "")
                        .replace("Di Salomone.<br>", "")
                        .replaceAll(".*l'Ezraita\\.<br>", "")
                        .replaceAll(".*Canto\\.<br>", "")
                        .replaceAll(".*Dei figli di Core\\.<br>", "")
                        .replace("Al maestro del coro.", "")
                        .replace("Di Davide.", "")
                        .replace("Maskil.<br>", "")
                        .replace("Di Davide, servo del Signore.<br>", "")
                        .replace("Dei figli di Core. Maskil.<br>", "")
                        .replace("Dei figli di Core.", "")
                        .replaceAll(".*Miktam\\.<br>", "")
                        .trim();
            }
            return result;
        } else {
            return testo;
        }
    }

    public static List<HashMap> estraiLiturgiaFestiva(Date data, boolean epifaniaFestiva, CalendarioDAO dao) {
        String giornoMese = Util.giornoMese(data);
        List<HashMap> liturgiaGiornoEsatto = dao.liturgiaFestivaGiorno(giornoMese);
        if (liturgiaGiornoEsatto == null || liturgiaGiornoEsatto.isEmpty()) {
            Pair<String, Integer> liturgiaPeriodo1 = Util.liturgiafestiva(data, epifaniaFestiva);
            if (liturgiaPeriodo1 != null) {
                return dao.liturgiaFestiva(liturgiaPeriodo1.getVal0(), liturgiaPeriodo1.getVal1());
            }
        }
        return liturgiaGiornoEsatto;
    }

    public static List<HashMap> estraiLiturgiaFeriale(Date data, boolean epifaniaFestiva, CalendarioDAO dao) {
        String giornoMese = Util.giornoMese(data);
        List<HashMap> liturgiaGiornoEsatto = dao.liturgiaFerialeGiorno(giornoMese);
        if (liturgiaGiornoEsatto == null || liturgiaGiornoEsatto.isEmpty()) {
            Triple<String, Integer, Integer> liturgiaPeriodo1 = Util.liturgiaFeriale(data, epifaniaFestiva);
            if (liturgiaPeriodo1 != null) {
                return dao.liturgiaFeriale(liturgiaPeriodo1.getVal0(), liturgiaPeriodo1.getVal1(), liturgiaPeriodo1.getVal2());
            }
        }
        return liturgiaGiornoEsatto;
    }

    public static List<List<HashMap>> estraiCodLiturgia(Date data, boolean epifaniaFestiva, CalendarioDAO dao, String lingua) {
        List<List<HashMap>> result = new ArrayList<>();
        List<HashMap> liturgieFestive = estraiLiturgiaFestiva(data, epifaniaFestiva, dao);
        String cicloFestivoLetture = Util.getCicloFestivoLetture(data);
        String cicloFerialeLetture = Util.getCicloFerialeLetture(data);
        if (liturgieFestive == null || liturgieFestive.isEmpty()) {
            List<HashMap> liturgieFeriali = estraiLiturgiaFeriale(data, epifaniaFestiva, dao);
            for (HashMap m : liturgieFeriali) {
                result.add(dao.liturgiaFerialeCodice((Integer) m.get("id"), cicloFerialeLetture, lingua));
            }
        } else {
            for (HashMap m : liturgieFestive) {
                result.add(dao.liturgiaFestivaCodice((Integer) m.get("id"), cicloFestivoLetture, lingua));
            }
        }
        return result;
    }

    public static String estraiNomeLiturgia(Date data, boolean epifaniaFestiva, CalendarioDAO dao, String lingua) {
        String nomeGiorno = "";
        try {
            List<List<HashMap>> codLiturgia = estraiCodLiturgia(data, epifaniaFestiva, dao, lingua);
            if (codLiturgia.size() > 0) {
                List<HashMap> lit = codLiturgia.get(codLiturgia.size() - 1);
                nomeGiorno = (String) lit.get(0).get("nome");
                nomeGiorno = nomeGiorno.replaceAll(": Messa$", "")
                        .replaceAll(": Messa del Giorno", "")
                        .replaceAll(": Mass$", "")
                        .replaceAll(": Mass during the Day", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Data: " + Util.formattaDataNomeFile(data) + " " + e.getMessage());
        }
        return nomeGiorno;
    }

    public static List<Triple<String, String, String>> estraiElencoCapitoliLetturaGiorno(Date data, boolean epifaniaFestiva, CalendarioDAO dao, String lingua) {
        List<Triple<String, String, String>> result = new ArrayList<>();
        try {
            List<List<HashMap>> codLiturgia = estraiCodLiturgia(data, epifaniaFestiva, dao, lingua);
            for (List<HashMap> lit : codLiturgia) {
                for (HashMap lettura : lit) {
                    String codiceLettura = (String) lettura.get("codice_lettura");
                    List<Lettura> letture = estraiLettureDaCodice(codiceLettura, false, dao, lingua);
                    if (letture != null && letture.size() > 0) {
                        Lettura l = letture.get(0);
                        String autore = l.getAutore();
                        String descrizione = dao.abbreviazioneDaCodLibro(autore, lingua);
                        int cap = l.getCapitoloLettura().get(0).getCapitolo();
                        descrizione += " " + cap;
                        List<Quadruple<String, String, String, String>> versetti = l.getCapitoloLettura().get(0).getVersetti();
                        String versetto = null;
                        if (versetti != null && versetti.size() > 0) {
                            versetto = versetti.get(0).getVal0();
                            descrizione += "," + versetto;
                        }
                        Triple<String, String, String> tripletta = new Triple<>(autore + "_" + cap, "#" + versetto, descrizione);
                        result.add(tripletta);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Triple<String, String, String>> estraiElencoCapitoliCodice(String codiceLettura, CalendarioDAO dao, String lingua) {
        List<Triple<String, String, String>> result = new ArrayList<>();
        try {
            List<Lettura> letture = estraiLettureDaCodice(codiceLettura, true, dao, lingua);
            for (Lettura l : letture) {
                String autore = l.getAutore();
                String descrizione = dao.abbreviazioneDaCodLibro(autore, lingua);
                int cap = l.getCapitoloLettura().get(0).getCapitolo();
                descrizione += " " + cap;
                List<Quadruple<String, String, String, String>> versetti = l.getCapitoloLettura().get(0).getVersetti();
                String versetto = null;
                if (versetti != null && versetti.size() > 0) {
                    versetto = versetti.get(0).getVal0();
                    descrizione += "," + versetto;
                }
                Triple<String, String, String> tripletta = new Triple<>(autore + "_" + cap, "#" + versetto, descrizione);
                result.add(tripletta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String estraiTestoLetturaGiorno(Date data, boolean epifaniaFestiva, boolean mostraVersetti, CalendarioDAO dao, String lingua, String versioneBibbia) {
        String testoHtml = "";
        testoHtml += "<h4>" + Util.capitalizza(Util.formattaDataFull(data, lingua)) + "</h4>";
        List<List<HashMap>> codLiturgia = estraiCodLiturgia(data, epifaniaFestiva, dao, lingua);
        if (codLiturgia.size() > 0) {
            for (List<HashMap> lit : codLiturgia) {
                if (lit.size() > 0) {
                    String nomeGiorno = (String) lit.get(0).get("nome");
                    testoHtml += "<h3>" + nomeGiorno + "</h3>\n";
                    for (HashMap lettura : lit) {
                        String codiceLettura = (String) lettura.get("codice_lettura");
                        String tipoLettura = (String) lettura.get("tipo_lettura");
                        String idDiv = "testo";
                        if (codiceLettura != null) {
                            idDiv = codiceLettura.startsWith("Sal") ? "testo" : "testo";
                        }
                        String testoLettura = estraiTestoLetturaDaCodice(codiceLettura, mostraVersetti, dao, versioneBibbia, lingua, tipoLettura);
                        String descrizione = (String) lettura.get("des_lettura");
                        String desLocalizzata = estraiDescrizioneLocalizzata(descrizione, dao, lingua);
                        testoHtml += "<div class=\"titoli\">" + lettura.get("descrizione")
                                + " (" + desLocalizzata + ")</div>\n" + "<div class=\"" + idDiv
                                + "\">" + testoLettura + "</div>\n";
                    }
                }
            }
        }
        return testoHtml.trim();
    }

    public static TestoLettureGiornoConfronto estraiConfrontoLetturaGiorno(Context context, Date data, boolean epifaniaFestiva,
                                                                           CalendarioDAO dao, String versioneSinistra,
                                                                           String versioneDestra, String terzaVersione, String lingua) {
        TestoLettureGiornoConfronto testoLettureGiornoConfronto = new TestoLettureGiornoConfronto();
        List<String> items = new ArrayList<>(Arrays.asList(Cache.getBibbieEntries(context)));
        List<String> itemsCode = new ArrayList<>(Arrays.asList(Cache.getBibbieEntriesValues(context)));
        HashMap<String, List<Versetto>> testo1 = new LinkedHashMap<>();
        HashMap<String, List<Versetto>> testo2 = new LinkedHashMap<>();
        HashMap<String, List<Versetto>> testo3 = new LinkedHashMap<>();
        testoLettureGiornoConfronto.setTesto1(testo1);
        testoLettureGiornoConfronto.setTesto2(testo2);
        testoLettureGiornoConfronto.setTesto3(terzaVersione != null ? testo3 : null);
        testoLettureGiornoConfronto.setDirezionePrima(versioneSinistra.equals("ebraico") ? "testoebraico" : "testoLatino");
        testoLettureGiornoConfronto.setDirezioneSeconda(versioneDestra.equals("ebraico") ? "testoebraico" : "testoLatino");
        testoLettureGiornoConfronto.setTitoloPrimabibbia(items.get(itemsCode.indexOf(versioneSinistra)));
        testoLettureGiornoConfronto.setTitoloSecondabibbia(items.get(itemsCode.indexOf(versioneDestra)));
        if (terzaVersione != null) {
            testoLettureGiornoConfronto.setDirezioneTerza(terzaVersione.equals("ebraico") ? "testoebraico" : "testoLatino");
            testoLettureGiornoConfronto.setTitoloTerzabibbia(items.get(itemsCode.indexOf(terzaVersione)));
        }
        List<List<HashMap>> codLiturgia = estraiCodLiturgia(data, epifaniaFestiva, dao, lingua);
        if (codLiturgia.size() > 0) {
            for (List<HashMap> lit : codLiturgia) {
                if (lit.size() > 0) {
                    for (HashMap lettura : lit) {
                        String codiceLettura = (String) lettura.get("codice_lettura");
                        String tipoLettura = (String) lettura.get("tipo_lettura");
                        String descrizione = (String) lettura.get("des_lettura");
                        String desLocalizzata = estraiDescrizioneLocalizzata(descrizione, dao, lingua);
                        testo1.put(lettura.get("descrizione") + " (" + desLocalizzata + ")", estraiVersettiLetturaDaCodice(codiceLettura, dao, versioneSinistra, lingua, tipoLettura));
                        testo2.put(lettura.get("descrizione") + " (" + desLocalizzata + ")", estraiVersettiLetturaDaCodice(codiceLettura, dao, versioneDestra, lingua, tipoLettura));
                        if (terzaVersione != null) {
                            testo3.put(lettura.get("descrizione") + " (" + desLocalizzata + ")", estraiVersettiLetturaDaCodice(codiceLettura, dao, terzaVersione, lingua, tipoLettura));
                        }
                    }
                }
            }
        }

        return testoLettureGiornoConfronto;

    }

    public static String estraiTestoLetturaRicerca(String codice, boolean mostraVersetti, CalendarioDAO dao, String versioneBibbia, String lingua) {
        String testoHtml = "";
        List<Lettura> lett = estraiLettureDaCodice(codice, true, dao, lingua);
        for (Lettura l : lett) {
            if (l.getAutore() != null) {
                String idDiv = l.getAutore().startsWith("Sal") ? "testosinistra" : "testosinistra";
                String testoLettura = estraiTestoLetturaDaLettura(l, mostraVersetti, dao, versioneBibbia, "V");
                testoHtml += "<div class=\"titoli\">" + l.getDescrizione()
                        + "</div>\n" + "<div class=\"" + idDiv
                        + "\"><p>" + testoLettura + "</p></div>\n";
            }
        }
        return testoHtml.trim();
    }

    public static TestoLettureGiornoConfronto estraiConfrontoLetturaRicerca(Context context, String codice, boolean mostraVersetti,
                                                                            CalendarioDAO dao, String versioneSinistra,
                                                                            String versioneDestra, String terzaVersione,
                                                                            String lingua) {
        TestoLettureGiornoConfronto testoLettureGiornoConfronto = new TestoLettureGiornoConfronto();
        List<String> items = new ArrayList<>(Arrays.asList(Cache.getBibbieEntries(context)));
        List<String> itemsCode = new ArrayList<>(Arrays.asList(Cache.getBibbieEntriesValues(context)));
        List<Lettura> lett = estraiLettureDaCodice(codice, true, dao, lingua);
        HashMap<String, List<Versetto>> testo1 = new LinkedHashMap<>();
        HashMap<String, List<Versetto>> testo2 = new LinkedHashMap<>();
        HashMap<String, List<Versetto>> testo3 = new LinkedHashMap<>();
        testoLettureGiornoConfronto.setTesto1(testo1);
        testoLettureGiornoConfronto.setTesto2(testo2);
        testoLettureGiornoConfronto.setTesto3(terzaVersione != null ? testo3 : null);
        testoLettureGiornoConfronto.setDirezionePrima(versioneSinistra.equals("ebraico") ? "testoebraico" : "testoLatino");
        testoLettureGiornoConfronto.setDirezioneSeconda(versioneDestra.equals("ebraico") ? "testoebraico" : "testoLatino");
        testoLettureGiornoConfronto.setTitoloPrimabibbia(items.get(itemsCode.indexOf(versioneSinistra)));
        testoLettureGiornoConfronto.setTitoloSecondabibbia(items.get(itemsCode.indexOf(versioneDestra)));
        if (terzaVersione != null) {
            testoLettureGiornoConfronto.setDirezioneTerza(terzaVersione.equals("ebraico") ? "testoebraico" : "testoLatino");
            testoLettureGiornoConfronto.setTitoloTerzabibbia(items.get(itemsCode.indexOf(terzaVersione)));
        }
        for (Lettura l : lett) {
            if (l.getAutore() != null) {
                testo1.put(l.getDescrizione(), estraiVersettiLetturaDaLettura(l, dao, versioneSinistra, "V"));
                testo2.put(l.getDescrizione(), estraiVersettiLetturaDaLettura(l, dao, versioneDestra, "V"));
                if (terzaVersione != null) {
                    testo3.put(l.getDescrizione(), estraiVersettiLetturaDaLettura(l, dao, terzaVersione, "V"));
                }
            }
        }
        return testoLettureGiornoConfronto;
    }

    public static String intestazioneHtml(String stiliAggiuntivi, String testoHtml, boolean clickVersetti, String linguaTesto, boolean hypenate) {
        String linguaAttr = "";
        if (Regex.stringaNonVuota(linguaTesto)) {
            linguaAttr = " lang=\"" + linguaTesto + "\"";
        }
        return "<!DOCTYPE html> <html" + linguaAttr + "> <head> <meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=device-width,user-scalable=no\">" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "style.css" + "\" />" +
                stiliAggiuntivi +
                (clickVersetti ? "<script src=\"myload.js\"></script>" : "") +
                "<script src=\"myscripts.js\"></script>" +
                (hypenate ? "<script src=\"hyphenate.js\" type=\"text/javascript\"></script>" : "") +
                "</head><body" + (hypenate ? " class=\"hyphenate\"" : "") + ">\n" +
                testoHtml +
                "\n</body></html>";
    }

    public static String intestazioneHtmlConfronto(String stiliAggiuntivi, String testoHtml) {
        return "<!DOCTYPE html> <html> <head> <meta charset=\"UTF-8\"> <meta name=\"viewport\" content=\"width=device-width,user-scalable=no\">" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + "style-confronto.css" + "\" />" +
                stiliAggiuntivi +
                "<script src=\"myscripts.js\"></script>" +
                "</head><body>\n" +
                testoHtml +
                "\n</body></html>";
    }

    public static String testoHtmlConfrontaLettura(Context c,TestoCapitoloPerConfronto testoCapitoloPerConfronto,
                                                   int modalitaNotte) {

        List<Versetto> primoTesto = new ArrayList<>(testoCapitoloPerConfronto.getTesto1().values());
        List<Versetto> secondoTesto = new ArrayList<>(testoCapitoloPerConfronto.getTesto2().values());
        List<Versetto> terzoTesto = testoCapitoloPerConfronto.getTesto3() == null ? null : new ArrayList<>(testoCapitoloPerConfronto.getTesto3().values());

        String titoloPrimabibbia = testoCapitoloPerConfronto.getTitoloPrimabibbia();
        String titoloSecondabibbia = testoCapitoloPerConfronto.getTitoloSecondabibbia();
        String titoloTerzabibbia = testoCapitoloPerConfronto.getTitoloTerzabibbia();

        String direzionePrima = testoCapitoloPerConfronto.getDirezionePrima();
        String direzioneSeconda = testoCapitoloPerConfronto.getDirezioneSeconda();
        String direzioneTerza = testoCapitoloPerConfronto.getDirezioneTerza();

        String tabella = "<table>\n" +
                "  <tr>\n" +
                "    <th>" + titoloPrimabibbia + "</th>\n" +
                "    <th>" + titoloSecondabibbia + "</th> \n" +
                (titoloTerzabibbia != null ? "    <th>" + titoloTerzabibbia + "</th>\n" : "") +
                "  </tr>\n";
        int maxIndice = Math.max(Math.max(primoTesto.size(), secondoTesto.size()), terzoTesto != null ? terzoTesto.size() : 0);
        for (int i = 0; i < maxIndice; i++) {
            Versetto testo1 = i < primoTesto.size() ? primoTesto.get(i) : new Versetto();
            Versetto testo2 = i < secondoTesto.size() ? secondoTesto.get(i) : new Versetto();
            Versetto testo3 = null;
            if (terzoTesto != null) {
                testo3 = i < terzoTesto.size() ? terzoTesto.get(i) : new Versetto();
            }
            tabella += "  <tr>\n" +
                    "    <td class=\"" + direzionePrima + "\"><sup>" + testo1.getNumero() + "</sup>" + testo1.getTesto() + "</td>\n" +
                    "    <td class=\"" + direzioneSeconda + "\"><sup>" + testo2.getNumero() + "</sup>" + testo2.getTesto() + "</td>\n" +
                    (terzoTesto != null ? "    <td class=\"" + direzioneTerza + "\"><sup>" + testo3.getNumero() + "</sup>" + testo3.getTesto() + "</td>\n" : "") +
                    "  </tr>\n";
        }
        tabella += "</table>";

        String stiliAggiuntivi = Css.getColoreStyle(c,modalitaNotte);
        return intestazioneHtmlConfronto(stiliAggiuntivi, tabella);
    }

    public static String testoHtmlConfrontaLettureGiorno(TestoLettureGiornoConfronto testoCapitoloPerConfronto) {

        List<String> primoTestoTitoli = new ArrayList<>(testoCapitoloPerConfronto.getTesto1().keySet());
        List<String> secondoTestoTitoli = new ArrayList<>(testoCapitoloPerConfronto.getTesto2().keySet());
        List<String> terzoTestoTitoli = testoCapitoloPerConfronto.getTesto3() == null ? null : new ArrayList<>(testoCapitoloPerConfronto.getTesto3().keySet());

        String titoloPrimabibbia = testoCapitoloPerConfronto.getTitoloPrimabibbia();
        String titoloSecondabibbia = testoCapitoloPerConfronto.getTitoloSecondabibbia();
        String titoloTerzabibbia = testoCapitoloPerConfronto.getTitoloTerzabibbia();

        String direzionePrima = testoCapitoloPerConfronto.getDirezionePrima();
        String direzioneSeconda = testoCapitoloPerConfronto.getDirezioneSeconda();
        String direzioneTerza = testoCapitoloPerConfronto.getDirezioneTerza();

        String tabella = "<table>\n" +
                "  <tr>\n" +
                "    <th>" + titoloPrimabibbia + "</th>\n" +
                "    <th>" + titoloSecondabibbia + "</th> \n" +
                (titoloTerzabibbia != null ? "    <th>" + titoloTerzabibbia + "</th>\n" : "") +
                "  </tr>\n";
        String colspan = terzoTestoTitoli == null ? "2" : "3";
        int maxIndice = Math.max(Math.max(primoTestoTitoli.size(), secondoTestoTitoli.size()), terzoTestoTitoli != null ? terzoTestoTitoli.size() : 0);
        for (int i = 0; i < maxIndice; i++) {
            String testoTitolo1 = i < primoTestoTitoli.size() ? primoTestoTitoli.get(i) : "";
            List<Versetto> primoTesto = i < primoTestoTitoli.size() ? testoCapitoloPerConfronto.getTesto1().get(testoTitolo1) : new ArrayList<Versetto>();
            String testoTitolo2 = i < secondoTestoTitoli.size() ? secondoTestoTitoli.get(i) : "";
            List<Versetto> secondoTesto = i < secondoTestoTitoli.size() ? testoCapitoloPerConfronto.getTesto2().get(testoTitolo2) : new ArrayList<Versetto>();
            String testoTitolo3;
            List<Versetto> terzoTesto = new ArrayList<>();
            if (terzoTestoTitoli != null) {
                testoTitolo3 = i < terzoTestoTitoli.size() ? terzoTestoTitoli.get(i) : "";
                terzoTesto = i < terzoTestoTitoli.size() ? testoCapitoloPerConfronto.getTesto3().get(testoTitolo3) : new ArrayList<Versetto>();
            }
            tabella += "  <tr>\n" +
                    "    <th colspan=\"" + colspan + "\">" + testoTitolo1 + "</th>\n" +
                    "  </tr>\n";

            int maxIndiceV = Math.max(Math.max(primoTesto.size(), secondoTesto.size()), terzoTesto.size());
            for (int j = 0; j < maxIndiceV; j++) {
                Versetto testo1 = j < primoTesto.size() ? primoTesto.get(j) : new Versetto();
                Versetto testo2 = j < secondoTesto.size() ? secondoTesto.get(j) : new Versetto();
                Versetto testo3 = null;
                if (terzoTestoTitoli != null) {
                    testo3 = j < terzoTesto.size() ? terzoTesto.get(j) : new Versetto();
                }
                tabella += "  <tr>\n" +
                        "    <td class=\"" + direzionePrima + "\"><sup>" + testo1.getNumero() + "</sup>" + testo1.getTesto() + "</td>\n" +
                        "    <td class=\"" + direzioneSeconda + "\"><sup>" + testo2.getNumero() + "</sup>" + testo2.getTesto() + "</td>\n" +
                        (terzoTestoTitoli != null ? "    <td class=\"" + direzioneTerza + "\"><sup>" + testo3.getNumero() + "</sup>" + testo3.getTesto() + "</td>\n" : "") +
                        "  </tr>\n";
            }
        }
        tabella += "</table>";

        return tabella;
    }
}
