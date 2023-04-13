package com.barisi.flavio.bibbiacattolica.fragments;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;

import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.adapter.ModificaFontAdapter;
import com.barisi.flavio.bibbiacattolica.calendario.DataUtils;
import com.barisi.flavio.bibbiacattolica.database.DatabaseHelper;
import com.barisi.flavio.bibbiacattolica.database.Inizializzazione;
import com.barisi.flavio.bibbiacattolica.gui.Css;
import com.barisi.flavio.bibbiacattolica.interfaces.NotaSegnalibroListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnZoomPressed;
import com.barisi.flavio.bibbiacattolica.interfaces.UpdateableFragment;
import com.barisi.flavio.bibbiacattolica.model.CapitoloText;
import com.barisi.flavio.bibbiacattolica.model.TestoCapitoloPerConfronto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("deprecation")
public class LeggiCapitoloFragment extends LeggiFragment implements OnZoomPressed, UpdateableFragment {

    private static final String ARG_ARTICLE_ID = "categoryId";
    private static final String ARG_PAROLA_CERCARE = "parolaDaCercare";
    private static final String ARG_CRON = "salvaCron";

    private String idCapitolo;
    private boolean seArticoloPreferito;
    private WebView noteView;
    private CapitoloText capitoloText;
    private String testoPulito;
    private String testoWebView;
    private List<String> numeriVersetti;
    private String parolaDaCercare;
    private boolean mostraVersetti;
    private TestoCapitoloPerConfronto testoCapitoloPerConfronto;

    //private MyTimingLogger logger;


    public LeggiCapitoloFragment() {
        // Required empty public constructor
    }

    public static LeggiCapitoloFragment newInstance(String articleId, Boolean salvaCronologia, String parolaDaCercare) {
        LeggiCapitoloFragment fragment = new LeggiCapitoloFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_ID, articleId);
        args.putString(ARG_PAROLA_CERCARE, parolaDaCercare);
        args.putBoolean(ARG_CRON, salvaCronologia);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //logger = new MyTimingLogger("Performance","LeggiCapitoloFragment");
        if (getArguments() != null) {
            idCapitolo = getArguments().getString(ARG_ARTICLE_ID);
            parolaDaCercare = getArguments().getString(ARG_PAROLA_CERCARE);
        }
        if (getArguments().getBoolean(ARG_CRON) && idCapitolo != null && !idCapitolo.endsWith("_0")) {
            Preferenze.aggiungiCronologia(getActivity(), idCapitolo);
        }
        setHasOptionsMenu(true);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        mostraVersetti = Preferenze.ottieniMostraVersetti(getContext());
        aCapo = Preferenze.ottieniACapo(getContext());
        testoGiustificato = Preferenze.ottieniTestoGiustificato(getContext());
        noteView = (WebView) getActivity().findViewById(R.id.note);
        articleView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (parolaDaCercare != null) {
                    if (parolaDaCercare.startsWith("#")) {
                        String numeroVersetto = parolaDaCercare.substring(1);
                        if (parolaDaCercare.contains("-")) {
                            numeroVersetto = parolaDaCercare.substring(1, parolaDaCercare.indexOf("-"));
                        }
                        Utility.eseguiJavascript(articleView, "myScrollToTagHtml('sup','" + numeroVersetto + "');");
                    } else {
                        Utility.eseguiJavascript(articleView, "myScrollTo('highlighted');");
                    }
                }
            }
        });
        sfondoWebView(noteView);

        articleView.addJavascriptInterface(new LeggiCapitoloFragment.WebViewJavaScriptInterface(), "app");

        visualizza();
        return v;
    }

    protected void visualizzaTestoSingolo() {
        showProgressBar();
        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                seArticoloPreferito = serviziDatabase.sePreferito(idCapitolo);
                try {
                    Inizializzazione.caricaDatabaseSeNecessario(getActivity(), versioneSinistra);
                    capitoloText = serviziDatabase.testoCapitolo(idCapitolo, versioneSinistra);
                    //logger.addSplit("testoCapitolo");
                    List<String> versettiPreferiti = serviziDatabase.listaVersettiPreferitiPerCapitolo(idCapitolo);
                    numeriVersetti = Regex.numeriVersetti(capitoloText.getTesto());
                    SortedSet<Integer> versettiDaEvidenziare = new TreeSet<>();
                    for (String v : versettiPreferiti) {
                        if (v.contains("-")) {
                            int numeroVersettoStart = numeriVersetti.indexOf(v.substring(0, v.indexOf("-")));
                            int numeroVersettoEnd = numeriVersetti.indexOf(v.substring(v.indexOf("-") + 1));
                            if (numeroVersettoStart != -1 && numeroVersettoEnd != -1) {
                                for (int j = numeroVersettoStart; j <= numeroVersettoEnd; j++) {
                                    versettiDaEvidenziare.add(j);
                                }
                            }
                        }
                    }
                    if (capitoloText.getTesto() != null) {
                        Boolean mostraTitoli = Preferenze.ottieniMostraTitoli(getContext());
                        String testo = Regex.replaceString(capitoloText.getTesto(), "<br>", "<br> ");
                        //Metti l'icona dei segnalibri sul singolo versetto
                        for (String v : versettiPreferiti) {
                            if (!v.contains("-")) {
                                testo = testo.replace("<sup>" + v + "</sup>", "<sup class=\"bookmark\">" + v + "</sup>");
                            }
                        }
                        //Evidenzia intervalli di versetti
                        if (versettiDaEvidenziare.size() > 0) {
                            for (Integer vv : versettiDaEvidenziare) {
                                String vers = numeriVersetti.get(vv);
                                testo = testo.replaceAll("(?s)(<sup( class=\"bookmark\")?>" + vers + "</sup>(?:(?!<sup).)+)", "<span id=\"highlighted2\">$1</span>");
                            }
                            if (testo.contains("class=\"rientrato\">")) {
                                testo = testo.replaceAll("(?s)(<span id=\"highlighted2\">(?:(?!<span).)+<p class=\"rientrato\">)((?:(?!<span).)+</span>)", "$1<span id=\"highlighted2\">$2</span>");
                            }
                        }
                        //Nascondi i titoli
                        if (!mostraTitoli) {
                            testo = Regex.eliminaTitoliCapitoli(testo);
                        }
                        //Evidenzia le parole o le frasi cercate
                        if (parolaDaCercare != null) {
                            if (!parolaDaCercare.startsWith("#")) {
                                if (parolaDaCercare.startsWith("\"")) {
                                    String p = parolaDaCercare.replaceAll("[.,!?\"<>]", "");
                                    if (!p.equals("\\w*\\w*")) {
                                        testo = testo.replaceAll("(?i)\\b(?<!<)(" + p + ")(?!>)(?!=)(?!\">)\\b", "<span id=\"highlighted\">$1</span>");
                                    }
                                } else if (parolaDaCercare.startsWith("!")) {
                                    String[] parole1 = parolaDaCercare.substring(1).split("\\s+");
                                    List<String> parole = Regex.listaParoleIgnoraAccenti(parole1, testo);
                                    for (String aParole : parole) {
                                        testo = testo.replaceAll("(?i)\\b(?<!<)(" + aParole + ")(?!>)(?!=)(?!\">)\\b", "<span id=\"highlighted\">$1</span>");
                                    }
                                } else {
                                    String[] parole1 = parolaDaCercare.split("\\s+");
                                    List<String> parole = Arrays.asList(parole1);
                                    for (String aParole : parole) {
                                        testo = testo.replaceAll("(?i)\\b(?<!<)(" + aParole + ")(?!>)(?!=)(?!\">)\\b", "<span id=\"highlighted\">$1</span>");
                                    }
                                }
                            }
                        }

                        testoWebView = testo;
                        testoPulito = Html.fromHtml(Regex.eliminaTitoliCapitoli(Regex.eliminaVersetti(capitoloText.getTesto()))).toString().trim();
                    }

                } catch (Exception e) {
                    capitoloText = new CapitoloText();
                    capitoloText.setNomeLibro("");
                    testoPulito = null;
                    e.printStackTrace();
                    return "";
                }
                return null;
            }

            @Override
            protected void onPostExecute(String t) {
                try {
                    ricaricaWebView();
                } catch (Exception e) {
                    //
                }
            }
        };
        task.execute();
    }

    protected void ricaricaWebView() {
        try {
            String stiliAggiuntivi;
            String testoFinito;
            if (modalitaConfronto) {
                testoFinito = DataUtils.testoHtmlConfrontaLettura(getContext(), testoCapitoloPerConfronto, modalitaNotte);
                articleView.loadDataWithBaseURL("file:///android_asset/", testoFinito, "text/html", "UTF-8", null);
                hideProgressBar();
                int zoomDefault = Preferenze.zoomDefault(getContext());
                articleView.getSettings().setTextZoom(zoomDefault);
                sfondoWebView(articleView);
                getActivity().invalidateOptionsMenu();
                if (numeriVersetti.size() > 5) {
                    fastScroller.setWebView(articleView);
                    fastScroller.setVersetti(numeriVersetti);
                    fastScroller.setAppBar(appBar);
                    fastScroller.setSectionIndicator(sectionTitleIndicator);
                }
            } else {
                stiliAggiuntivi = Css.getMostraVersettiStyle(mostraVersetti) +
                        Css.getColoreStyle(getContext(), modalitaNotte) +
                        Css.getACapoStyle(aCapo) +
                        Css.getTestoGiustificatoStyle(testoGiustificato);
                String linguaBibbia = DatabaseHelper.databaseLanguage(getContext(), getVersioneBibbia());
                boolean hypenate = Regex.stringaNonVuota(linguaBibbia) && testoGiustificato == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                testoFinito = DataUtils.intestazioneHtml(stiliAggiuntivi, testoWebView, true, linguaBibbia, hypenate);
                articleView.loadDataWithBaseURL("file:///android_asset/", testoFinito, "text/html", "UTF-8", null);
                hideProgressBar();
                int zoomDefault = Preferenze.zoomDefault(getContext());
                articleView.getSettings().setTextZoom(zoomDefault);
                sfondoWebView(articleView);
                getActivity().invalidateOptionsMenu();
                if (numeriVersetti.size() > 5 && mostraVersetti) {
                    fastScroller.setWebView(articleView);
                    fastScroller.setVersetti(numeriVersetti);
                    fastScroller.setAppBar(appBar);
                    fastScroller.setSectionIndicator(sectionTitleIndicator);
                } else if (isDisplayingIntroduzione() || (numeriVersetti.size() > 5 && !mostraVersetti)) {
                    fastScroller.setWebView(articleView);
                    fastScroller.setVersetti(new ArrayList<String>());
                    fastScroller.setAppBar(appBar);
                    fastScroller.setSectionIndicator(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void visualizzaConfronto() {
        showProgressBar();
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    seArticoloPreferito = serviziDatabase.sePreferito(idCapitolo);
                    Inizializzazione.caricaDatabaseSeNecessario(getActivity(), versioneDestra);
                    Inizializzazione.caricaDatabaseSeNecessario(getActivity(), versioneSinistra);
                    if (terzaVersione != null) {
                        Inizializzazione.caricaDatabaseSeNecessario(getActivity(), terzaVersione);
                    }
                    capitoloText = serviziDatabase.testoCapitolo(idCapitolo, versioneSinistra);
                    numeriVersetti = Regex.numeriVersetti(capitoloText.getTesto());
                    testoCapitoloPerConfronto = serviziDatabase.testoCapitoloPerConfronto(idCapitolo, versioneSinistra, versioneDestra, terzaVersione);
                    testoPulito = Html.fromHtml(Regex.eliminaTitoliCapitoli(Regex.eliminaVersetti(capitoloText.getTesto()))).toString().trim();
                } catch (Exception e) {
                    testoPulito = null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String t) {
                try {
                    ricaricaWebView();
                } catch (Exception e) {
                    //
                }
            }

        };
        task.execute();
    }

    @Override
    protected View getHintIndicator() {
        return hintIndicator;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (isDisplayingIntroduzione()) {
            inflater.inflate(R.menu.introduction, menu);
        } else if (modalitaConfronto) {
            inflater.inflate(R.menu.confronta_new, menu);
        } else {
            inflater.inflate(R.menu.article, menu);
        }

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (capitoloText != null) {
                if (capitoloText.getNumero() != 0) {
                    if (capitoloText.getId().startsWith("Sal_")) {
                        actionBar.setTitle(String.format(getString(R.string.salmo), capitoloText.getNumero()));
                    } else {
                        actionBar.setTitle(String.format(getString(R.string.capitolo), capitoloText.getNumero()));
                    }
                } else {
                    actionBar.setTitle(getString(R.string.introduzione));
                }
                actionBar.setSubtitle(capitoloText.getNomeLibro());
            } else {
                actionBar.setTitle("");
                actionBar.setSubtitle("");
            }
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem favouriteButton = menu.findItem(R.id.action_favourite);
        if (favouriteButton != null) {
            if (seArticoloPreferito) {
                favouriteButton.setIcon(R.drawable.ic_star_white_24dp);
                favouriteButton.setTitle(R.string.rimuovi_segnalibri);
            } else {
                favouriteButton.setIcon(R.drawable.ic_star_outline_white_24dp);
                favouriteButton.setTitle(R.string.salva_segnalibri);
            }
        }
        MenuItem noteButton = menu.findItem(R.id.action_note);
        if (noteButton != null) {
            noteButton.setTitle(noteView.getVisibility() == WebView.VISIBLE ? R.string.nascondi_note : R.string.mostra_note);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share: {
                if (testoPulito != null) {
                    String testo = testoPulito;
                    testo += "\n\n" + getString(R.string.inviato_da_app);
                    testo += "\n\nhttps://play.google.com/store/apps/details?id=com.barisi.flavio.bibbiacattolica";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, testo);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, capitoloText.getNomeLibro() + ", " + capitoloText.getNumero());
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                }
                return true;
            }
            case R.id.action_favourite: {
                if (!seArticoloPreferito) {
                    serviziDatabase.salvarePreferito(idCapitolo, null);
                    seArticoloPreferito = true;
                    getActivity().invalidateOptionsMenu();
                    Utility.mostraSnackBar(getContext(), coordinatorLayoutView, getActivity().getString(R.string.messaggio_articolo_aggiunto), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.mostraDialogoOttieniTesto(getContext(), getView(), null, new NotaSegnalibroListener() {
                                @Override
                                public void noteInserted(String nota) {
                                    serviziDatabase.cancellarePreferito(idCapitolo);
                                    serviziDatabase.salvarePreferito(idCapitolo, nota);
                                }
                            });
                        }
                    }, R.string.segnalibro_nota);
                } else {
                    serviziDatabase.cancellarePreferito(idCapitolo);
                    seArticoloPreferito = false;
                    getActivity().invalidateOptionsMenu();
                    Utility.mostraSnackBar(getContext(), coordinatorLayoutView, getActivity().getString(R.string.messaggio_articolo_rimosso));
                }
                SegnalibriFragment.preferitiModificati = true;
                return true;
            }
            case R.id.action_font: {
                View b = getActivity().findViewById(R.id.action_font);
                if (b != null) {
                    ListPopupWindow pop = new ListPopupWindow(this.getContext());
                    ListAdapter ad = new ModificaFontAdapter(getContext(), articleView.getSettings().getTextZoom(), testoGiustificato, aCapo, this);
                    pop.setAnchorView(b);
                    pop.setAdapter(ad);
                    pop.setModal(true);
                    pop.setContentWidth(getResources().getDimensionPixelSize(R.dimen.popup));
                    pop.show();
                }
                return true;
            }
            case R.id.action_salva_intervallo_versetti: {
                showDialog2();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int zoomPlus() {
        int currentZoom = articleView.getSettings().getTextZoom();
        articleView.getSettings().setTextZoom(currentZoom + 10);
        noteView.getSettings().setTextZoom(Regex.round(0.75 * (currentZoom + 10), 1));
        int nuovoZoom = articleView.getSettings().getTextZoom();
        Preferenze.salvaZoom(getContext(), nuovoZoom);
        if (getParentFragment() != null) {
            ((LeggiAltaVoceFragment) getParentFragment()).updateFragments();
        }
        return nuovoZoom;
    }

    @Override
    public int zoomMinus() {
        int currentZoom = articleView.getSettings().getTextZoom();
        articleView.getSettings().setTextZoom(currentZoom - 10);
        noteView.getSettings().setTextZoom(Regex.round(0.75 * (currentZoom - 10), 1));
        int nuovoZoom = articleView.getSettings().getTextZoom();
        Preferenze.salvaZoom(getContext(), nuovoZoom);
        if (getParentFragment() != null) {
            ((LeggiAltaVoceFragment) getParentFragment()).updateFragments();
        }
        return nuovoZoom;
    }

    @Override
    public int zoomReset() {
        Preferenze.resetZoom(getContext());
        int zoomDefault = Preferenze.zoomDefault(getContext());
        articleView.getSettings().setTextZoom(zoomDefault);
        noteView.getSettings().setTextZoom(Regex.round(0.75 * zoomDefault, 1));
        if (getParentFragment() != null) {
            ((LeggiAltaVoceFragment) getParentFragment()).updateFragments();
        }
        return zoomDefault;
    }

    @Override
    public void modalitaNotte(int mod) {
        super.modalitaNotte(mod);
        sfondoWebView(noteView);
        if (getParentFragment() != null) {
            ((LeggiCapitoloParentFragment) getParentFragment()).aggiornaNote();
            ((LeggiAltaVoceFragment) getParentFragment()).updateFragments();
        }
    }

    @Override
    public void mostraACapo(int modalita) {
        Preferenze.salvaACapo(getContext(), modalita);
        aCapo = modalita;
        Utility.eseguiJavascript(articleView, "mostraACapo('" + modalita + "');");
        if (getParentFragment() != null) {
            ((LeggiAltaVoceFragment) getParentFragment()).updateFragments();
        }
    }

    @Override
    public void testoGiustificato(int modalita) {
        Preferenze.salvaTestoGiustificato(getContext(), modalita);
        testoGiustificato = modalita;
        visualizza();
        if (getParentFragment() != null) {
            ((LeggiAltaVoceFragment) getParentFragment()).updateFragments();
        }
    }

    private void showDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.scegliVersetti));
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_salva_versetti, (ViewGroup) getView(), false);
        final Spinner spinner1 = (Spinner) viewInflated.findViewById(R.id.spinner1);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, numeriVersetti);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
        final Spinner spinner2 = (Spinner) viewInflated.findViewById(R.id.spinner2);
        builder.setView(viewInflated);
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String versetti = spinner1.getSelectedItem() + "-" + spinner2.getSelectedItem();
                            salvaRangeVersettiSegnalibro(idCapitolo, versetti);
                            SegnalibriFragment.preferitiModificati = true;
                        } catch (Exception e) {
                            Utility.mostraSnackBar(getContext(), coordinatorLayoutView, getString(R.string.errore_des));
                        }
                    }
                }

        );

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> lista2 = numeriVersetti.subList(position, numeriVersetti.size());
                ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, lista2);
                dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(dataAdapter2);
                if (lista2.size() == 0) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void salvaSegnalibro(final String id, final String versetto) {
        AsyncTask<String, Void, Spanned> task = new AsyncTask<String, Void, Spanned>() {
            @Override
            protected Spanned doInBackground(String... strings) {
                serviziDatabase.salvaVersettoPreferito(id, versetto, null);
                SegnalibriFragment.preferitiModificati = true;
                return null;
            }

            @Override
            protected void onPostExecute(Spanned spanned) {
                if (getActivity() != null) {
                    Utility.mostraSnackBar(getContext(), coordinatorLayoutView, getString(R.string.aggiuntoSegnalibroVersetto, versetto), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.mostraDialogoOttieniTesto(getContext(), getView(), null, new NotaSegnalibroListener() {
                                @Override
                                public void noteInserted(String nota) {
                                    serviziDatabase.cancellareVersettoPreferito(id, versetto);
                                    serviziDatabase.salvaVersettoPreferito(id, versetto, nota);
                                }
                            });
                        }
                    }, R.string.segnalibro_nota);
                }
            }
        };
        task.execute();
    }

    private void salvaRangeVersettiSegnalibro(final String id, final String range) {
        AsyncTask<String, Void, Spanned> task = new AsyncTask<String, Void, Spanned>() {
            @Override
            protected Spanned doInBackground(String... strings) {
                serviziDatabase.cancellareVersettoPreferito(id, range);
                serviziDatabase.salvaVersettoPreferito(id, range, null);
                return null;
            }

            @Override
            protected void onPostExecute(Spanned spanned) {
                if (getActivity() != null) {
                    Utility.mostraSnackBar(getContext(), coordinatorLayoutView, getString(R.string.aggiuntoSegnalibroVersetti, range), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.mostraDialogoOttieniTesto(getContext(), getView(), null, new NotaSegnalibroListener() {
                                @Override
                                public void noteInserted(String nota) {
                                    serviziDatabase.cancellareVersettoPreferito(id, range);
                                    serviziDatabase.salvaVersettoPreferito(id, range, nota);
                                }
                            });
                        }
                    }, R.string.segnalibro_nota);
                    articleView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            Utility.eseguiJavascript(articleView, "myScrollToTagHtml('sup','" + range.substring(0, range.indexOf("-")) + "');");
                        }
                    });
                    visualizza();
                }
            }
        };
        task.execute();
    }

    private void cancellaSegnalibro(final String id, final String versetto) {
        AsyncTask<String, Void, Spanned> task = new AsyncTask<String, Void, Spanned>() {
            @Override
            protected Spanned doInBackground(String... strings) {
                serviziDatabase.cancellareVersettoPreferito(id, versetto);
                SegnalibriFragment.preferitiModificati = true;
                return null;
            }

            @Override
            protected void onPostExecute(Spanned spanned) {
                if (getActivity() != null) {
                    Utility.mostraSnackBar(getContext(), coordinatorLayoutView, getString(R.string.rimossoSegnalibroVersetto, versetto));
                }
            }
        };
        task.execute();
    }

    @SuppressWarnings("unused")
    private class WebViewJavaScriptInterface {

        @JavascriptInterface
        public void aggiungiSegnalibro(String versetto) {
            salvaSegnalibro(idCapitolo, versetto);
        }

        @JavascriptInterface
        public void rimuoviSegnalibro(String versetto) {
            cancellaSegnalibro(idCapitolo, versetto);
        }

    }

    public String getTestoPulito() {
        return testoPulito;
    }

    @Override
    public boolean isDisplayingIntroduzione() {
        return idCapitolo.endsWith("_0");
    }


    @Override
    public void update() {
        if (getContext() != null && articleView != null) {
            boolean refresh = false;
            int zoomDefault = Preferenze.zoomDefault(getContext());
            int aC = Preferenze.ottieniACapo(getContext());
            int tG = Preferenze.ottieniTestoGiustificato(getContext());
            int modNotte = Preferenze.ottieniModalitaNotte(getContext());
            if (modalitaNotte != modNotte) {
                modalitaNotte = modNotte;
                sfondoWebView(articleView);
                refresh = true;
            }
            articleView.getSettings().setTextZoom(zoomDefault);
            if (aC != aCapo) {
                Utility.eseguiJavascript(articleView, "mostraACapo('" + aC + "');");
                aCapo = aC;
            }
            if (tG != testoGiustificato) {
                Utility.eseguiJavascript(articleView, "testoGiustificato('" + tG + "');");
                testoGiustificato = tG;
            }
            if (modalitaConfronto != ((LeggiAltaVoceFragment) getParentFragment()).modalitaConfronto ||
                    (versioneDestra != null && !versioneDestra.equals(((LeggiAltaVoceFragment) getParentFragment()).versioneDestra)) ||
                    !versioneSinistra.equals(((LeggiAltaVoceFragment) getParentFragment()).versioneSinistra) ||
                    (terzaVersione == null && ((LeggiAltaVoceFragment) getParentFragment()).terzaVersione != null) ||
                    (terzaVersione != null && !terzaVersione.equals(((LeggiAltaVoceFragment) getParentFragment()).terzaVersione)) ||
                    refresh) {
                modalitaConfronto = ((LeggiAltaVoceFragment) getParentFragment()).modalitaConfronto;
                versioneDestra = ((LeggiAltaVoceFragment) getParentFragment()).versioneDestra;
                versioneSinistra = ((LeggiAltaVoceFragment) getParentFragment()).versioneSinistra;
                terzaVersione = ((LeggiAltaVoceFragment) getParentFragment()).terzaVersione;
                visualizza();
            }
        }
    }


}
