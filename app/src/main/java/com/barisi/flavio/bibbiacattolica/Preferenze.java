package com.barisi.flavio.bibbiacattolica;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class Preferenze {

    private static SharedPreferences sP;

    private static SharedPreferences ottieniDefaultSharedPreferences(Context c) {
        if (sP == null) {
            sP = PreferenceManager.getDefaultSharedPreferences(c);
        }
        return sP;
    }

    private static final String PREFS_NAME = "MyPrefsFile";

    public static int versioneApplicativo(Context c) {
        return c.getSharedPreferences(PREFS_NAME, 0).getInt("versionCode", -1);
    }

    public static void aggiornaVersioneApplicativo(Context c, int nuovaVersione) {
        c.getSharedPreferences(PREFS_NAME, 0).edit().putInt("versionCode", nuovaVersione).apply();
    }

    public static int versioneEspansione(Context c) {
        return c.getSharedPreferences(PREFS_NAME, 0).getInt("versioneEspansione", -1);
    }

    public static void aggiornaVersioneEspansione(Context c, int nuovaVersione) {
        c.getSharedPreferences(PREFS_NAME, 0).edit().putInt("versioneEspansione", nuovaVersione).apply();
    }

    public static List<String> cronologia(Context c) {
        String cron = c.getSharedPreferences(PREFS_NAME, 0).getString("cronologia", "");
        return Arrays.asList(TextUtils.split(cron, "-"));
    }

    public static void aggiungiCronologia(Context c, String elemento) {
        List<String> cron = cronologia(c);
        List<String> cronNew = new ArrayList<>();
        cronNew.add(elemento);
        for (int i = 0; i < cron.size() && i < 4; i++) {
            if (!cronNew.contains(cron.get(i)))
                cronNew.add(cron.get(i));
        }
        c.getSharedPreferences(PREFS_NAME, 0).edit().putString("cronologia", TextUtils.join("-", cronNew)).apply();
    }

    public static void cancellaCronologia(Context c) {
        c.getSharedPreferences(PREFS_NAME, 0).edit().putString("cronologia", "").apply();
    }

    public static boolean seHintVisto(String tipo, Context c) {
        return c.getSharedPreferences(PREFS_NAME, 0).getBoolean(tipo, false);
    }

    public static void settaHintVisto(String tipo, Context c) {
        c.getSharedPreferences(PREFS_NAME, 0).edit().putBoolean(tipo, true).apply();
    }

    /*private static void cancellaHintVisto(String tipo, Context c) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(tipo);
        editor.apply();
    }*/

    static void configuraImpostazioniDefault(Context c) {
        SharedPreferences sp = ottieniDefaultSharedPreferences(c);
        if (!sp.contains("acc_hardware")) {
            ottieniDefaultSharedPreferences(c).edit().putBoolean("acc_hardware", Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT).apply();
        }
        if (!sp.contains("accenti_greco")) {
            ottieniDefaultSharedPreferences(c).edit().putBoolean("accenti_greco", Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP).apply();
        }
        if (!sp.contains("lingua")) {
            String linguaPreferenze = ottieniLinguaDaLocale();
            salvaLingua(c, linguaPreferenze);
            salvaLinguaDispositivo(c, linguaPreferenze);
        }
        //Non più usati
        //Preferenze.cancellaHintVisto("hint_leggi_capitolo", c);
        //Preferenze.cancellaHintVisto("hint_lista_segnalibri", c);
        //Preferenze.cancellaHintVisto("hint_leggi_capitolo2", c);
        //Preferenze.cancellaHintVisto("hint_lista_capitoli", c);
        //Preferenze.cancellaHintVisto("hint_lista_libri", c);
        //Preferenze.cancellaHintVisto("hint_confronto", c);
        //Preferenze.cancellaHintVisto("translit_greek", c);
        //c.getSharedPreferences(PREFS_NAME, 0).edit().remove("primaVolta").apply();
    }

    public static String ottieniLinguaDaLocale() {
        //String lingua = Locale.getDefault().getLanguage();
        String lingua = Resources.getSystem().getConfiguration().locale.getLanguage();
        if (lingua.equals("it"))
            return "it";
        else
            return "en";
    }

    public static void reimpostaPreferenze(Context c) {
        ottieniDefaultSharedPreferences(c).edit().clear().apply();
        configuraImpostazioniDefault(c);
    }

    public static void salvaZoom(Context c, int zoom) {
        ottieniDefaultSharedPreferences(c).edit().putInt("zoom_default", zoom).apply();
    }

    public static int zoomDefault(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("zoom_default", zoomIniziale(c));
    }


    public static void resetZoom(Context c) {
        ottieniDefaultSharedPreferences(c).edit().remove("zoom_default").apply();
    }

    private static int zoomIniziale(Context c) {
        float scale = c.getResources().getConfiguration().fontScale;
        return Regex.round(scale * 120, 10);
    }

    public static void salvaModalitaNotte(Context c, int modalitaNotte) {
        ottieniDefaultSharedPreferences(c).edit().putInt("modalita_notte", modalitaNotte).apply();
    }

    public static int ottieniModalitaNotte(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("modalita_notte", 0);
    }

    public static void salvaACapo(Context c, int modalita) {
        ottieniDefaultSharedPreferences(c).edit().putInt("a_capo", modalita).apply();
    }

    public static int ottieniACapo(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("a_capo", 0);
    }


    public static void salvaACapoLettureGiorno(Context c, int modalita) {
        ottieniDefaultSharedPreferences(c).edit().putInt("a_capo_letture", modalita).apply();
    }

    public static int ottieniACapoLettureGiorno(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("a_capo_letture", 1);
    }

    public static void salvaTestoGiustificato(Context c, int modalita) {
        ottieniDefaultSharedPreferences(c).edit().putInt("testo_giustificato", modalita).apply();
    }

    public static int ottieniTestoGiustificato(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("testo_giustificato", 0);
    }

    public static void salvaTestoGiustificatoLettureGiorno(Context c, int modalita) {
        ottieniDefaultSharedPreferences(c).edit().putInt("testo_giustificato_letture", modalita).apply();
    }

    public static int ottieniTestoGiustificatoLettureGiorno(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("testo_giustificato_letture", 0);
    }

    static void salvaImpostaPreferenzeDefault(Context c, int preferenzeDefault) {
        ottieniDefaultSharedPreferences(c).edit().putInt("imposta_preferenze_default", preferenzeDefault).apply();
    }

    static int ottieniImpostaPreferenzeDefault(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("imposta_preferenze_default", 0);
    }

    public static String ottieniVersioneBibbia(Context c) {
        return ottieniDefaultSharedPreferences(c).getString("pref_vers_bibbia", c.getString(R.string.imp_vers_bibbia_default));
    }

    public static void salvaVersioneBibbia(Context c, String versione) {
        ottieniDefaultSharedPreferences(c).edit().putString("pref_vers_bibbia", versione).apply();
    }

    public static String ottieniLinguaPreghiere(Context c) {
        return ottieniDefaultSharedPreferences(c).getString("pref_lingua_preghiere", c.getString(R.string.imp_lingua_preghiere_default));
    }

    public static void salvaLinguaPreghiere(Context c, String versione) {
        ottieniDefaultSharedPreferences(c).edit().putString("pref_lingua_preghiere", versione).apply();
    }

    static String ottieniTema(Context c) {
        return ottieniDefaultSharedPreferences(c).getString("pref_tema", c.getString(R.string.imp_tema_default));
    }

    static void salvaTema(Context c, String tema) {
        ottieniDefaultSharedPreferences(c).edit().putString("pref_tema", tema).apply();
    }

    public static int ottieniColorePrimario(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("pref_colore", c.getResources().getColor(R.color.colorPrimary1));
    }

    public static int colorePrincipale(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorePrincipale, typedValue, true);
        return typedValue.data;
    }

    public static int coloreSecondario(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.coloreSecondario, typedValue, true);
        return typedValue.data;
    }

    public static int coloreSpeciale(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.coloreSpeciale, typedValue, true);
        return typedValue.data;
    }

    public static int coloreAccent(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    public static int iconafabPause(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.iconaFabPause, typedValue, true);
        return typedValue.resourceId;
    }

    public static int iconafabPlay(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.iconaFabPlay, typedValue, true);
        return typedValue.resourceId;
    }

    public static int coloreCard(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.coloreCard, typedValue, true);
        return typedValue.data;
    }

    public static int colorePrincipaleScuro(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorePrincipaleScuro, typedValue, true);
        return typedValue.data;
    }

    public static boolean ottieniEpifaniaFestivaLiturgia(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("pref_litugia_epifania_festiva", true);
    }

    public static boolean ottieniMostraVersettiLiturgia(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("pref_liturgia_versetti", false);
    }

    public static String ottieniLingua(Context c) {
        return ottieniDefaultSharedPreferences(c).getString("lingua", c.getString(R.string.imp_lingua_default));
    }

    static void salvaLingua(Context c, String lingua) {
        ottieniDefaultSharedPreferences(c).edit().putString("lingua", lingua).apply();
    }

    static String ottieniLinguaDispositivo(Context c) {
        return ottieniDefaultSharedPreferences(c).getString("lingua_dispositivo", null);
    }

    static void salvaLinguaDispositivo(Context c, String lingua) {
        ottieniDefaultSharedPreferences(c).edit().putString("lingua_dispositivo", lingua).apply();
    }

    static boolean ottieniNascondiToolbar(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("pref_nascondi_toolbar", true);
    }

    public static boolean ottieniMostraVersetti(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("pref_versetti", true);
    }

    public static boolean ottieniMostraTitoli(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("pref_titoli", true);
    }

    public static boolean ottieniAccellerazioneHardware(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("acc_hardware", false);
    }

    static boolean ottieniMostraAccentiGreco(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("accenti_greco", false);
    }

    public static boolean ottieniMostraNote(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("pref_note", false);
    }

    public static void salvaMostraNote(Context c, boolean mostraNote) {
        ottieniDefaultSharedPreferences(c).edit().putBoolean("pref_note", mostraNote).apply();
    }

    public static boolean ottieniLeggiAltaVoce(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("pref_leggi_alta_voce", false);
    }

    public static void salvaLeggiAltaVoce(Context c, boolean leggiAltaVoce) {
        ottieniDefaultSharedPreferences(c).edit().putBoolean("pref_leggi_alta_voce", leggiAltaVoce).apply();
    }

    static boolean ottieniSvuotaCronologia(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("pref_svuota_cronologia", false);
    }

    public static boolean ottieniMostraNumCapitoli(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("pref_mostra_num_capitoli", true);
    }

    public static int ottieniVisualizzazionieLibri(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("pref_visualizzazione_libri", 0);
    }

    public static void salvaVisualizzazionieLibri(Context c, int visualizzazione) {
        ottieniDefaultSharedPreferences(c).edit().putInt("pref_visualizzazione_libri", visualizzazione).apply();
    }

    public static int ottieniVisualizzazionieCapitoli(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("pref_visualizzazione_capitoli", 0);
    }

    public static void salvaVisualizzazionieCapitoli(Context c, int visualizzazione) {
        ottieniDefaultSharedPreferences(c).edit().putInt("pref_visualizzazione_capitoli", visualizzazione).apply();
    }

    public static boolean ottieniSchermoAcceso(Context c) {
        return ottieniDefaultSharedPreferences(c).getBoolean("pref_schermo_acceso", true);
    }

    public static String ottieniCapitoloCasuale(Context c) {
        return ottieniDefaultSharedPreferences(c).getString("pref_capitolo_casuale", "%");
    }

    public static float ottieniVelocitàLettura(Context c) {
        return Float.parseFloat(ottieniDefaultSharedPreferences(c).getString("pref_velocita_lettura", "1.0f"));
    }

    public static int ottieniColoreEvidenziazione(Context c) {
        return ottieniDefaultSharedPreferences(c).getInt("pref_colore_evidenziazione", c.getResources().getColor(R.color.colore_evidenziazione1));
    }

}
