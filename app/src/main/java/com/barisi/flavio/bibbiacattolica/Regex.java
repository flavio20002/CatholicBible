package com.barisi.flavio.bibbiacattolica;


import android.util.Log;

import com.barisi.flavio.bibbiacattolica.model.CapitoloLetture;
import com.barisi.flavio.bibbiacattolica.model.Lettura;
import com.barisi.flavio.bibbiacattolica.model.Quadruple;
import com.barisi.flavio.bibbiacattolica.model.Versetto;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    public static String eliminaVersetti(String testo) {
        return testo.replaceAll("<sup>\\d+</sup>", "");
    }

    public static String eliminaTitoliCapitoli(String testo) {
        return testo.replaceAll("(?s)<div class=\"titoli\">.*?</div>", "");
    }

    public static String eliminaTitoliLetture(String testo) {
        String t= testo.replaceAll("(?s)<div class=\"titoli\">.*?</div>", "");
        t= t.replaceAll("(?s)<h3>.*?</h3>", "");
        t= t.replaceAll("(?s)<h4>.*?</h4>", "");
        return t;
    }

    public static String eliminaTitoliLettureGiorno(String testo) {
        Log.i("eliminaTitoliLetture_p",testo);
        String t= testo.replaceAll("(?s)<div class=\"titoli\">(.*?) \\(.*?\\)</div>", "$1.");
        t= t.replaceAll("(?s)<h3>.*?</h3>", "");
        t= t.replaceAll("(?s)<h4>.*?</h4>", "");
        Log.i("eliminaTitoliLetture_d",t);
        return t;
    }

    public static HashMap<Integer, Versetto> versettiPerConfronto(String testo) {
        String test = eliminaTitoliCapitoli(testo);
        test = replaceString(test, "<sup>", "_sup_");
        test = replaceString(test, "</sup>", "_/sup_");
        test = Html.fromHtml(test).toString();
        test = replaceString(test, "\n", " ").replaceAll(" +", " ").trim();
        LinkedHashMap<Integer, Versetto> result = new LinkedHashMap<>();
        Pattern pattern = Pattern.compile("(?s)_sup_(\\d+).{0,2}_/sup_((?:(?!_sup_).)+)");
        Matcher matcher = pattern.matcher(test);
        while (matcher.find()) {
            String t = matcher.group(2).trim();
            int n = Integer.valueOf(matcher.group(1));
            Versetto ver = new Versetto();
            ver.setNumero(String.valueOf(n));
            if (result.containsKey(n)) {
                ver.setTesto(result.get(n).getTesto() + " " + t);
            } else {
                ver.setTesto(t);
            }
            result.put(n, ver);
        }
        return result;
    }

    public static HashMap<String, String> versettiPerConfronto2(String testo) {
        String test = eliminaTitoliCapitoli(testo);
        test = replaceString(test, "<sup>", "_sup_");
        test = replaceString(test, "</sup>", "_/sup_");
        test = Html.fromHtml(test).toString();
        test = replaceString(test, "\n", " ").replaceAll(" +", " ").trim();
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        Pattern pattern = Pattern.compile("(?s)_sup_(\\d+.{0,2})_/sup_((?:(?!_sup_).)+)");
        Matcher matcher = pattern.matcher(test);
        while (matcher.find()) {
            String n = matcher.group(1);
            String t = matcher.group(2).trim();
            result.put(n, t);
        }
        return result;
    }

    static public String replaceString(String orig, String search, String replace) {
        int cap = replace.length() > search.length() ? orig.length() + (replace.length() - search.length()) * 20 : orig.length();
        StringBuilder out = new StringBuilder(cap);

        int prev = 0;
        CharSequence okPart;
        for (int i = orig.indexOf(search); i != -1; i = orig.indexOf(search, prev)) {
            okPart = orig.subSequence(prev, i);
            out.append(okPart).append(replace);
            prev = i + search.length();
        }
        if (prev < orig.length()) {
            okPart = orig.subSequence(prev, orig.length());
            out.append(okPart);
        }
        return out.toString();
    }

    public static List<String> numeriVersetti(String testo) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?s)<sup>(\\d+.{0,2})</sup>");
        Matcher matcher = pattern.matcher(testo);
        while (matcher.find()) {
            String t = matcher.group(1);
            result.add(t);
        }
        return result;
    }


    public static HashMap<Integer, HashMap<String, Versetto>> versettiPerLetture(String testo, boolean eol) {
        String test = eliminaTitoliCapitoli(testo);
        test = replaceString(test, "<sup>", "_sup_");
        test = replaceString(test, "</sup>", "_/sup_");
        if (eol) {
            test = replaceString(test, "<br>", "\\n");
            test = replaceString(test, "</p>", "\\n");
        }
        test = Html.fromHtml(test).toString();
        test = replaceString(test, "\n", " ");
        test = replaceString(test, "  ", " ");
        if (eol) {
            test = replaceString(test, "\\n", "<br>");
            test = replaceString(test, "<br> ", "<br>");
            test = replaceString(test, " <br>", "<br>");
            test = replaceString(test, "<br><br>", "<br>");
            test = replaceString(test, "<br><br>", "<br>");
            test = replaceString(test, "<br>", "<br> ");
        }
        LinkedHashMap<Integer, HashMap<String, Versetto>> result = new LinkedHashMap<>();
        Pattern pattern = Pattern.compile("(?s)_sup_(\\d+)(.?)_/sup_((?:(?!_sup_).)+)");
        Matcher matcher = pattern.matcher(test);
        while (matcher.find()) {
            String t = matcher.group(3).trim();
            int n = Integer.valueOf(matcher.group(1));
            String lettera = matcher.group(2);
            Versetto ver = new Versetto();
            ver.setNumero(String.valueOf(n));
            ver.setTesto(t);
            if (result.containsKey(n)) {
                HashMap<String, Versetto> versett = result.get(n);
                versett.put(lettera, ver);
                result.put(n, versett);
            } else {
                LinkedHashMap<String, Versetto> versett = new LinkedHashMap<>();
                versett.put(lettera, ver);
                result.put(n, versett);
            }
        }
        return result;
    }

    public static HashMap<Integer, Versetto> versettiPerLettureInterna(HashMap<Integer, HashMap<String, Versetto>> vers, String start, String startLettera, String end, String endLettera) {
        Integer startI = Integer.valueOf(start);
        LinkedHashMap<Integer, Versetto> result = new LinkedHashMap<>();
        if (stringaVuota(end)) {
            Versetto a = concatenaVersetti(vers.get(startI), startLettera);
            if (a != null) {
                a.setNumero(start);
                result.put(startI, a);
            }
        } else if (start.equals(end) && stringaNonVuota(startLettera) && stringaNonVuota(endLettera)) {
            Versetto a = concatenaVersettiLettere(vers.get(startI), startLettera, endLettera);
            if (a != null) {
                a.setNumero(start);
                result.put(startI, a);
            }
        } else if (start.equals(end)) {
            Versetto a = concatenaVersetti(vers.get(startI), "");
            if (a != null) {
                a.setNumero(start);
                result.put(startI, a);
            }
        } else {
            boolean usa = false;
            for (Integer k : vers.keySet()) {
                if (k.equals(startI)) {
                    usa = true;
                    Versetto a = concatenaVersetti(vers.get(k), startLettera);
                    if (a != null) {
                        a.setNumero(String.valueOf(k)+startLettera);
                        result.put(k, a);
                    }
                } else if (!end.equals("*") && k.equals(Integer.valueOf(end))) {
                    usa = false;
                    Versetto a = concatenaVersetti(vers.get(k), endLettera);
                    if (a != null) {
                        a.setNumero(String.valueOf(k)+endLettera);
                        result.put(k, a);
                    }
                } else {
                    if (usa) {
                        Versetto a = concatenaVersetti(vers.get(k), "");
                        if (a != null) {
                            a.setNumero(String.valueOf(k));
                            result.put(k, a);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static Versetto concatenaVersettiLettere(HashMap<String, Versetto> versettos, String startLettera, String endLettera) {
        if (versettos != null) {
            String t = "";
            Set<String> lett = versettos.keySet();
            for (String s : lett) {
                if (s.compareTo(startLettera) >= 0 && s.compareTo(endLettera) <= 0) {
                    t += versettos.get(s).getTesto() + " ";
                }
            }
            Versetto v = new Versetto();
            v.setTesto(t.trim());
            return v;
        } else {
            return null;
        }
    }

    public static Versetto concatenaVersetti(HashMap<String, Versetto> versettos, String lettera) {
        if (versettos != null) {
            if (stringaVuota(lettera)) {
                return concatenaVersetti(versettos);
            } else {
                String t = "";
                for (char l : lettera.toCharArray()) {
                    String ls = String.valueOf(l);
                    if (versettos.containsKey(ls)) {
                        t += versettos.get(ls).getTesto() + " ";
                    }
                }
                if (stringaVuota(t)) {
                    char[] lettere = "abcdefghi".toCharArray();
                    String testo = concatenaVersetti(versettos).getTesto().replace(".<br>", "**");
                    String[] separat = testo.split("(?<=(\\.|<br>|\\*\\*|\\?))");
                    HashMap<String, String> temp1 = new LinkedHashMap<>();
                    for (int i = 0; i < separat.length; i++) {
                        temp1.put(String.valueOf(lettere[i]), separat[i].replace("**", ".<br>"));
                    }
                    for (char l : lettera.toCharArray()) {
                        String ls = String.valueOf(l);
                        if (temp1.containsKey(ls)) {
                            t += temp1.get(ls) + " ";
                        }
                    }
                }
                if (stringaVuota(t)) {
                    return concatenaVersetti(versettos);
                }
                Versetto v = new Versetto();
                v.setTesto(t.trim());
                return v;
            }
        } else {
            return null;
        }
    }

    public static Versetto concatenaVersetti(HashMap<String, Versetto> versettos) {
        if (versettos != null) {
            String t = "";
            String numero = "";
            Collection<Versetto> vvv = versettos.values();
            for (Versetto v : vvv) {
                t += v.getTesto() + " ";
                numero = v.getNumero();
            }
            Versetto v = new Versetto();
            v.setNumero(numero);
            v.setTesto(t.trim());
            return v;
        } else {
            return null;
        }
    }

    public static String versetto(String testo, String numeroVersetto) {
        String test = eliminaTitoliCapitoli(testo);
        Pattern pattern = Pattern.compile("(?s)<sup>" + numeroVersetto + "</sup>((?:(?!<sup>).)+)");
        Matcher matcher = pattern.matcher(test);
        if (matcher.find()) {
            String t = Html.fromHtml(matcher.group(1)).toString();
            t = t.replace("\n", " ").replaceAll(" +", " ").trim();
            return t;
        }
        return "";
    }

    public static List<String> paroleRicerca(String testo, boolean parolaEsatta) {
        Set<String> result = new HashSet<>();
        Pattern pattern = Pattern.compile("\\b\\p{L}{3,}");
        Matcher matcher = pattern.matcher(testo);
        while (matcher.find()) {
            if (!parolaEsatta && !testo.startsWith("\"")) {
                result.add(matcher.group(0) + "%");
            } else {
                result.add(matcher.group(0));
            }
        }
        return new ArrayList<>(result);
    }

    public static ArrayList<Lettura> decodificaLetture(String l) {
        ArrayList<Lettura> result = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile("((?<!\\p{Alpha})\\d?\\p{Alpha}{2,4}) *((?:\\d+(?!\\p{Alpha})[ ,]*(?:\\d+\\p{Alpha}*[- .]*(?!,))*[ ;]*)+)");
            Pattern pattern2 = Pattern.compile("(\\d+)[ ,]*([-.\\p{Alpha}\\d ]*(?!\\d*,))");
            Pattern pattern3 = Pattern.compile("(\\d+)(\\p{Alpha}*) *(-?) *(?:(\\d+)(\\p{Alpha}*))?");
            Matcher matcher = pattern.matcher(l);
            while (matcher.find()) {
                String autore = matcher.group(1);
                String capitoli = matcher.group(2);
                if (capitoli != null && !capitoli.equals("")) {
                    Lettura lettura = new Lettura();
                    result.add(lettura);
                    List<CapitoloLetture> cap = new ArrayList<>();
                    lettura.setAutore(autore);
                    lettura.setDescrizione(autore + " " + capitoli);
                    lettura.setCapitoloLettura(cap);
                    Matcher matcher2 = pattern2.matcher(capitoli);
                    boolean tuttiVersettiTemp = false;
                    while (matcher2.find()) {
                        CapitoloLetture cp = new CapitoloLetture();
                        cap.add(cp);
                        List<Quadruple<String, String, String, String>> v = new ArrayList<>();
                        cp.setVersetti(v);
                        cp.setCapitolo(Integer.valueOf(matcher2.group(1)));
                        String versetti = matcher2.group(2);
                        Matcher matcher3 = pattern3.matcher(versetti);
                        while (matcher3.find()) {
                            String start = matcher3.group(1);
                            String startLetter = matcher3.group(2);
                            String sep = matcher3.group(3);
                            String endS = matcher3.group(4);
                            String endSLetter = matcher3.group(5);
                            if (tuttiVersettiTemp) {
                                v.add(new Quadruple<>("1", "", start, startLetter));
                                tuttiVersettiTemp = false;
                            } else if (stringaVuota(sep)) {
                                v.add(new Quadruple<>(start, startLetter, "", ""));
                            } else if (stringaVuota(endS)) {
                                v.add(new Quadruple<>(start, startLetter, "*", ""));
                                tuttiVersettiTemp = true;
                            } else {
                                v.add(new Quadruple<>(start, startLetter, endS, endSLetter));
                            }
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static boolean stringaNonVuota(String s) {
        return s != null && !s.isEmpty();
    }

    public static boolean stringaVuota(String s) {
        return s == null || s.isEmpty();
    }

    public static int round(double i, int v) {
        return (int) Math.round(i / v) * v;
    }


    public static String rimuoviAccenti(String testo) {
        return Normalizer.normalize(testo, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String rimuoviAccentiRicerca(String testo) {
        String rimosso = rimuoviAccenti(testo);
        return rimosso.equals(testo) ? null : rimosso;
    }

    public static List<String> listaParoleIgnoraAccenti(String[] parole, String testo) {
        Set<String> risultato = new HashSet<>();
        String testoSenzaAccenti = Regex.rimuoviAccenti(testo);
        for (String parola : parole) {
            Pattern pattern = Pattern.compile("(?i)\\b(?<!<)(" + parola + ")(?!>)(?!=)(?!\">)\\b");
            Matcher matcher = pattern.matcher(testoSenzaAccenti);
            while (matcher.find()) {
                String laParola = testo.substring(matcher.start(), matcher.end());
                risultato.add(laParola);
            }
        }
        return new ArrayList<>(risultato);
    }


}
