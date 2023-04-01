package com.barisi.flavio.bibbiacattolica.calendario;


import com.barisi.flavio.bibbiacattolica.model.Pair;
import com.barisi.flavio.bibbiacattolica.model.Triple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Util {

    public static String capitalizza(String s) {
        if (s != null && s.length() > 0) {
            if (s.startsWith("<sup>")) {
                int index = s.indexOf("</sup>") + 6;
                return s.substring(0, index) + s.substring(index, index + 1).toUpperCase() + s.substring(index + 1);
            } else {
                return s.substring(0, 1).toUpperCase() + s.substring(1);
            }
        } else {
            return s;
        }
    }

    public static Date getDataCorrente() {
        return resetTime(new Date());
    }

    public static String formattaData(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }

    public static String formattaDataNomeFile(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(data);
    }

    public static String formattaDataFull(Date data, String lingua) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale(lingua));
        return sdf.format(data);
    }

    public static String giornoMese(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        return sdf.format(data);
    }

    public static Date interpretaStringa(String stringa) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.parse(stringa);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static int getAnno(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return Integer.valueOf(sdf.format(date));
    }

    public static int getGiornodelMese(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return Integer.valueOf(sdf.format(date));
    }

    public static Date domenicaPrecedente(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK) - 1));
        return cal.getTime();
    }

    public static Date domenicaSuccessiva(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.add(Calendar.DAY_OF_WEEK, 7 - (cal.get(Calendar.DAY_OF_WEEK) - 1));
        return cal.getTime();
    }

    public static boolean seDomenica(Date data) {
        Calendar date = Calendar.getInstance();
        date.setTime(data);
        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean seLunedi(Date data) {
        Calendar date = Calendar.getInstance();
        date.setTime(data);
        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return true;
        } else {
            return false;
        }
    }

    public static int giornoSettimana(Date data) {
        Calendar date = Calendar.getInstance();
        date.setTime(data);
        return date.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static Date aggiungiSettimane(Date data, int settimane) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.add(Calendar.WEEK_OF_YEAR, settimane);
        return cal.getTime();
    }

    public static Date aggiungiGiorni(Date data, int giorni) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.add(Calendar.DAY_OF_MONTH, giorni);
        return cal.getTime();
    }

    public static int getGiorniFraDueDate(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static int getSettimaneFraDueDate(Date a, Date b) {
        if (b.before(a)) {
            return -getSettimaneFraDueDate(b, a);
        }
        a = resetTime(a);
        b = resetTime(b);

        Calendar cal = new GregorianCalendar();
        cal.setTime(a);
        int weeks = 0;
        while (cal.getTime().before(b)) {
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            weeks++;
        }
        return weeks;
    }

    public static Date resetTime(Date d) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static String getCicloFestivoLetture(Date d) {
        int year = getAnno(d);
        Date inizioAvvento = inizioAvvento(year);
        if (d.equals(inizioAvvento) || d.after(inizioAvvento(year))) {
            year++;
        }
        int diff = (year - 2014) % 3;
        switch (diff) {
            case 0:
                return "A";
            case 1:
                return "B";
            default:
                return "C";
        }
    }

    public static String getCicloFerialeLetture(Date d) {
        int year = getAnno(d);
        Date inizioAvvento = inizioAvvento(year);
        if (d.equals(inizioAvvento) || d.after(inizioAvvento(year))) {
            year++;
        }
        int diff = (year - 2015) % 2;
        switch (diff) {
            case 0:
                return "1";
            default:
                return "2";
        }
    }

    public static Date getPasqua(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int n = (h + l - 7 * m + 114) / 31;
        int p = (h + l - 7 * m + 114) % 31;
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, n - 1, p + 1);
        return calendar.getTime();
    }

    public static Date getEpifania(int year, boolean epifaniaFesta) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        if (epifaniaFesta) {
            calendar.set(year, Calendar.JANUARY, 6);
            return calendar.getTime();
        } else {
            calendar.set(year, Calendar.JANUARY, 1);
            return domenicaSuccessiva(calendar.getTime());
        }
    }

    public static Date getNatale(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, Calendar.DECEMBER, 25);
        return calendar.getTime();
    }

    public static Date setteGennaio(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, Calendar.JANUARY, 7);
        return calendar.getTime();
    }

    public static Date ottoGennaio(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, Calendar.JANUARY, 8);
        return calendar.getTime();
    }

    public static Date inizioTempoOrdinarioBattesimoSignore(int year, boolean epifaniaFesta) {
        Date epifania = getEpifania(year, epifaniaFesta);
        if (epifania.equals(setteGennaio(year)) || epifania.equals(ottoGennaio(year))) {
            return (aggiungiGiorni(epifania, 1));
        } else {
            return domenicaSuccessiva(epifania);
        }
    }

    public static Date mercolediCeneri(int year) {
        return aggiungiGiorni(getPasqua(year), -46);
    }

    public static Date inizioQuaresima(int year) {
        return aggiungiSettimane(getPasqua(year), -6);
    }

    public static Date annunciazione(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, Calendar.MARCH, 25);
        Date dataAnnunciazione = calendar.getTime();
        Date pasqua = getPasqua(year);
        Date domenicaDopoPasqua = aggiungiSettimane(pasqua, 1);
        Date palme = aggiungiSettimane(pasqua, -1);
        Date inizioQuaresima = inizioQuaresima(year);
        for (int i = 0; i < 5; i++) {
            Date d = aggiungiSettimane(inizioQuaresima, i);
            if (d.equals(dataAnnunciazione)) {
                return aggiungiGiorni(d, 1);
            }
        }
        if (dataAnnunciazione.equals(domenicaDopoPasqua) || dataAnnunciazione.equals(palme) || (dataAnnunciazione.after(palme) && dataAnnunciazione.before(domenicaDopoPasqua))) {
            return aggiungiGiorni(aggiungiSettimane(pasqua, 1), 1);
        }
        return dataAnnunciazione;
    }

    public static Date sanGiuseppe(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, Calendar.MARCH, 19);
        Date dataSanGiuseppe = calendar.getTime();
        Date pasqua = getPasqua(year);
        Date palme = aggiungiSettimane(pasqua, -1);
        Date inizioQuaresima = inizioQuaresima(year);
        for (int i = 0; i < 5; i++) {
            Date d = aggiungiSettimane(inizioQuaresima, i);
            if (d.equals(dataSanGiuseppe)) {
                return aggiungiGiorni(dataSanGiuseppe, 1);
            }
        }
        if (dataSanGiuseppe.equals(palme) || dataSanGiuseppe.equals(pasqua) || (dataSanGiuseppe.after(palme) && dataSanGiuseppe.before(pasqua))) {
            return aggiungiGiorni(palme, -1);
        }
        return dataSanGiuseppe;
    }

    public static Date sacroCuoreGesu(int year) {
        return aggiungiGiorni(getPasqua(year), 68);
    }

    public static Date immacolatoCuoreMaria(int year) {
        return aggiungiGiorni(getPasqua(year), 69);
    }

    public static Date immacolataConcezione(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, Calendar.DECEMBER, 8);
        return calendar.getTime();
    }

    public static Date santaFamiglia(int year) {
        Date natale = getNatale(year);
        if (seDomenica(natale)) {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(year, Calendar.DECEMBER, 30);
            return calendar.getTime();
        } else {
            return domenicaSuccessiva(natale);
        }
    }

    public static Date secondaDomenicaDopoNatale(int year, boolean epifaniaFesta) {
        Date domSuccMaria = domenicaSuccessiva(festaMariaSantissima(year));
        if (domSuccMaria.before(getEpifania(year, epifaniaFesta))) {
            return domSuccMaria;
        } else {
            return null;
        }
    }

    private static Date festaMariaSantissima(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, Calendar.JANUARY, 1);
        return calendar.getTime();
    }

    public static Date inizioTempoOrdinarioPentecoste(int year) {
        return aggiungiSettimane(getPasqua(year), 7);
    }

    public static Date inizioAvvento(int year) {
        Date natale = getNatale(year);
        if (seDomenica(natale)) {
            return aggiungiSettimane(natale, -4);
        } else {
            return aggiungiSettimane(domenicaPrecedente(natale), -3);
        }
    }

    public static int domenicaTempoOrdinario(Date data, boolean epifaniaFesta) {
        Date domenicaPrec = domenicaPrecedente(data);
        int anno = getAnno(data);
        if (!epifaniaFesta &&
                seLunedi(data) &&
                domenicaPrec.equals(getEpifania(anno, epifaniaFesta)) &&
                (domenicaPrec.equals(setteGennaio(anno)) ||
                        domenicaPrec.equals(ottoGennaio(anno)))) {
            return 1;
        } else if (!seDomenica(data)) {
            return 0;
        } else {
            int numeroSettimaneInizioTempoOrdinario = getSettimaneFraDueDate(inizioTempoOrdinarioBattesimoSignore(anno, epifaniaFesta), data);
            int numeroSettimaneQuaresima = getSettimaneFraDueDate(inizioQuaresima(anno), data);
            int numeroSettimaneInizioTempoOrdinario2 = getSettimaneFraDueDate(inizioTempoOrdinarioPentecoste(anno), data);
            int numeroSettimaneAvvento = getSettimaneFraDueDate(inizioAvvento(anno), data);
            if (numeroSettimaneInizioTempoOrdinario >= 0 && numeroSettimaneQuaresima < 0) {
                return numeroSettimaneInizioTempoOrdinario + 1;
            } else if (numeroSettimaneInizioTempoOrdinario2 >= 0 && numeroSettimaneAvvento < 0) {
                return 35 + numeroSettimaneAvvento;
            } else {
                return 0;
            }
        }
    }

    public static int domenicaAvvento(Date data) {
        if (!seDomenica(data)) {
            return 0;
        } else {
            int anno = getAnno(data);
            int numeroSettimaneAvvento = getSettimaneFraDueDate(inizioAvvento(anno), data);
            if (numeroSettimaneAvvento >= 0 && numeroSettimaneAvvento < 4) {
                return numeroSettimaneAvvento + 1;
            } else {
                return 0;
            }
        }
    }

    public static int domenicaQuaresima(Date data) {
        if (!seDomenica(data)) {
            return 0;
        } else {
            int anno = getAnno(data);
            int numeroSettimaneQuaresima = getSettimaneFraDueDate(inizioQuaresima(anno), data);
            if (numeroSettimaneQuaresima >= 0 && numeroSettimaneQuaresima < 6) {
                return numeroSettimaneQuaresima + 1;
            } else {
                return 0;
            }
        }
    }

    public static int domenicaPasqua(Date data) {
        if (!seDomenica(data)) {
            return 0;
        } else {
            int anno = getAnno(data);
            int numeroSettimanePasqua = getSettimaneFraDueDate(getPasqua(anno), data);
            if (numeroSettimanePasqua >= 0 && numeroSettimanePasqua < 10) {
                return numeroSettimanePasqua + 1;
            } else {
                return 0;
            }
        }
    }

    public static int settimanaSanta(Date data) {
        if (seDomenica(data)) {
            return 0;
        } else {
            Date domSucc = domenicaSuccessiva(data);
            if (domSucc.equals(getPasqua(getAnno(data)))) {
                return giornoSettimana(data);
            } else {
                return 0;
            }
        }
    }

    public static int solennitaCalcolata(Date data, boolean epifaniaFesta) {
        Date dataR = resetTime(data);
        int anno = getAnno(data);
        if (dataR.equals(sacroCuoreGesu(anno))) {
            return 1;
        } else if (dataR.equals(getEpifania(anno, epifaniaFesta))) {
            return 2;
        } else if (dataR.equals(sanGiuseppe(anno))) {
            return 3;
        } else if (dataR.equals(annunciazione(anno))) {
            return 4;
        } else if (dataR.equals(immacolatoCuoreMaria(anno))) {
            return 5;
        } else if (dataR.equals(santaFamiglia(anno))) {
            return 6;
        } else if (dataR.equals(secondaDomenicaDopoNatale(anno, epifaniaFesta))) {
            return 7;
        } else if (dataR.equals(immacolataConcezione(anno))) {
            return 8;
        } else {
            return 0;
        }
    }

    public static Pair<String, Integer> liturgiafestiva(Date data, boolean epifaniaFesta) {
        int domQuaresima = domenicaQuaresima(data);
        if (domQuaresima > 0) {
            return new Pair<>("quaresima", domQuaresima);
        }
        int ceneri = ceneri(data);
        if (ceneri == 3) {
            return new Pair<>("ceneri", ceneri);
        }
        int settiSanta = settimanaSanta(data);
        if (settiSanta >= 4 && settiSanta <= 6) {
            return new Pair<>("settimanaSanta", settiSanta);
        }
        int domPasqua = domenicaPasqua(data);
        if (domPasqua > 0) {
            return new Pair<>("pasqua", domPasqua);
        }
        int domAvvento = domenicaAvvento(data);
        if (domAvvento > 0) {
            return new Pair<>("avvento", domAvvento);
        }
        int solCalcolata = solennitaCalcolata(data, epifaniaFesta);
        if (solCalcolata > 0) {
            return new Pair<>("calcolato", solCalcolata);
        }
        int domTempoOrdinario = domenicaTempoOrdinario(data, epifaniaFesta);
        if (domTempoOrdinario > 0) {
            return new Pair<>("ordinario", domTempoOrdinario);
        }
        return null;
    }

    public static int ceneri(Date data) {
        if (seDomenica(data)) {
            return 0;
        } else {
            Date domSucc = domenicaSuccessiva(data);
            if (domSucc.equals(inizioQuaresima(getAnno(data)))) {
                return giornoSettimana(data);
            } else {
                return 0;
            }
        }
    }

    public static Pair<Integer, Integer> ferialeQuaresima(Date data) {
        if (seDomenica(data)) {
            return null;
        } else {
            Date domSucc = domenicaPrecedente(data);
            int settQuar = domenicaQuaresima(domSucc);
            if (settQuar > 0 && settQuar <= 5) {
                return new Pair(settQuar, giornoSettimana(data));
            }
            return null;
        }
    }

    public static Pair<Integer, Integer> ferialePasqua(Date data) {
        if (seDomenica(data)) {
            return null;
        } else {
            Date domSucc = domenicaPrecedente(data);
            int settPasqua = domenicaPasqua(domSucc);
            if (settPasqua > 0 && settPasqua <= 7) {
                return new Pair(settPasqua, giornoSettimana(data));
            }
            return null;
        }
    }

    public static Pair<Integer, Integer> ferialeOrdinario(Date data, boolean epifaniaFesta) {
        if (seDomenica(data)) {
            return null;
        } else {
            Date domPrec = domenicaPrecedente(data);
            int settOrdinario = domenicaTempoOrdinario(domPrec, epifaniaFesta);
            boolean domPrecEpifaniaSetteOtto = Util.getEpifania(getAnno(data), epifaniaFesta).equals(domPrec) && (domPrec.equals(Util.setteGennaio(getAnno(data))) || domPrec.equals(Util.ottoGennaio(getAnno(data))));
            if (domPrecEpifaniaSetteOtto) {
                settOrdinario = 1;
            }
            if (settOrdinario > 0) {
                return new Pair(settOrdinario, giornoSettimana(data));
            }
            return null;
        }
    }

    public static Pair<Integer, Integer> ferialeAvvento(Date data) {
        if (seDomenica(data)) {
            return null;
        } else {
            Date domSucc = domenicaPrecedente(data);
            int settAvvento = domenicaAvvento(domSucc);
            if (settAvvento > 0 && settAvvento <= 3) {
                return new Pair(settAvvento, giornoSettimana(data));
            }
            return null;
        }
    }

    public static int ferialeTempoNatale(Date data, boolean epifaniaFesta) {
        if (!seDomenica(data)) {
            Date epifania = getEpifania(getAnno(data), epifaniaFesta);
            Date festaMariaSantissima = festaMariaSantissima(getAnno(data));
            if (data.after(festaMariaSantissima) && data.before(epifania)) {
                return getGiornodelMese(data);
            }
        }
        return 0;
    }

    public static int ferialeEpifania(Date data, boolean epifaniaFesta) {
        if (seDomenica(data)) {
            return 0;
        } else {
            Date epifania = getEpifania(getAnno(data), epifaniaFesta);
            for (int i = 1; i <= 6; i++) {
                Date d = aggiungiGiorni(epifania, i);
                if (data.equals(d)) {
                    return i;
                }
            }
            return 0;
        }
    }

    public static Triple<String, Integer, Integer> liturgiaFeriale(Date data, boolean epifaniaFesta) {
        int ceneri = ceneri(data);
        if (ceneri >= 4) {
            return new Triple<>("ceneri", 0, ceneri);
        }
        Pair<Integer, Integer> quaresima = ferialeQuaresima(data);
        if (quaresima != null) {
            return new Triple<>("quaresima", quaresima.getVal0(), quaresima.getVal1());
        }
        int settiSanta = settimanaSanta(data);
        if (settiSanta > 0 && settiSanta <= 3) {
            return new Triple<>("settimanaSanta", 0, settiSanta);
        }
        Pair<Integer, Integer> pasqua = ferialePasqua(data);
        if (pasqua != null) {
            return new Triple<>("pasqua", pasqua.getVal0(), pasqua.getVal1());
        }
        Pair<Integer, Integer> ordinario = ferialeOrdinario(data, epifaniaFesta);
        if (ordinario != null) {
            return new Triple<>("ordinario", ordinario.getVal0(), ordinario.getVal1());
        }

        Pair<Integer, Integer> avvento = ferialeAvvento(data);
        if (avvento != null) {
            return new Triple<>("avvento", avvento.getVal0(), avvento.getVal1());
        }
        int tempoNatale = ferialeTempoNatale(data, epifaniaFesta);
        if (tempoNatale > 0) {
            return new Triple<>("tempoNatale", tempoNatale, 0);
        }
        int epifania = ferialeEpifania(data, epifaniaFesta);
        if (epifania > 0) {
            return new Triple<>("epifania", 0, epifania);
        }
        return null;
    }

    public static List<Integer> listaGiorniInt(int numeroGiorni) {
        List<Integer> result = new ArrayList<>();
        for (int i = -numeroGiorni; i < numeroGiorni; i++) {
            result.add(i);
        }
        return result;
    }

    public static List<Date> listaGiorni(Date date, int numeroGiorni) {
        List<Date> result = new ArrayList<>();
        for (int i = -numeroGiorni; i < numeroGiorni; i++) {
            result.add(aggiungiGiorni(date, i));
        }
        return result;
    }

}
