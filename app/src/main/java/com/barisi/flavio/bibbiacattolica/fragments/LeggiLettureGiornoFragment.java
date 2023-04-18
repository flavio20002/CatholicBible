package com.barisi.flavio.bibbiacattolica.fragments;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.MainActivity;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.adapter.ModificaFontAdapter;
import com.barisi.flavio.bibbiacattolica.calendario.CalendarioDAOImpl;
import com.barisi.flavio.bibbiacattolica.calendario.DataUtils;
import com.barisi.flavio.bibbiacattolica.calendario.Util;
import com.barisi.flavio.bibbiacattolica.database.DatabaseHelper;
import com.barisi.flavio.bibbiacattolica.database.Inizializzazione;
import com.barisi.flavio.bibbiacattolica.gui.Css;
import com.barisi.flavio.bibbiacattolica.interfaces.OnZoomPressed;
import com.barisi.flavio.bibbiacattolica.interfaces.UpdateableFragment;
import com.barisi.flavio.bibbiacattolica.model.TestoLettureGiornoConfronto;
import com.barisi.flavio.bibbiacattolica.model.Triple;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("deprecation")
public class LeggiLettureGiornoFragment extends LeggiFragment implements OnZoomPressed, UpdateableFragment {

    private static final String ARG_LETTURE = "letture";
    private static final String ARG_DATA = "data";

    private String letture;
    private Date data;
    private CalendarioDAOImpl dao;
    private boolean forzaModalitaNotte = false;
    private String testoPulitoCondivisione;
    private String testoPulitoLettura;
    String testoHtml;


    public LeggiLettureGiornoFragment() {
        // Required empty public constructor
    }

    public static LeggiLettureGiornoFragment newInstance(String letture) {
        LeggiLettureGiornoFragment fragment = new LeggiLettureGiornoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LETTURE, letture);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(Date data) {
        LeggiLettureGiornoFragment fragment = new LeggiLettureGiornoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            letture = getArguments().getString(ARG_LETTURE);
            data = (Date) getArguments().getSerializable(ARG_DATA);
        }
        dao = new CalendarioDAOImpl(getContext());
        setHasOptionsMenu(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        aCapo = Preferenze.ottieniACapoLettureGiorno(getContext());
        testoGiustificato = Preferenze.ottieniTestoGiustificatoLettureGiorno(getContext());
        visualizza();
        return v;
    }


    @Override
    protected void visualizzaTestoSingolo() {
        showProgressBar();
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                testoPulitoCondivisione = null;
                testoPulitoLettura = null;
                try {
                    Inizializzazione.caricaDatabaseSeNecessario(getActivity(), versioneSinistra);
                    String lingua = Preferenze.ottieniLingua(getContext());
                    if (letture != null) {
                        try {
                            Boolean mostraVersetti = Preferenze.ottieniMostraVersetti(getContext());
                            testoHtml = DataUtils.estraiTestoLetturaRicerca(letture, mostraVersetti, dao, versioneSinistra, lingua);
                            testoPulitoCondivisione = Html.fromHtml(Regex.eliminaVersetti(testoHtml)).toString();
                            testoPulitoLettura = Html.fromHtml(Regex.eliminaTitoliLetture(Regex.eliminaVersetti(testoHtml))).toString().trim();
                        } catch (Exception e) {
                            testoHtml = getActivity().getString(R.string.riferimentoNonTrovato);
                        }
                    } else {
                        try {
                            boolean epifaniaFestiva = Preferenze.ottieniEpifaniaFestivaLiturgia(getContext());
                            boolean mostraVersetti = Preferenze.ottieniMostraVersettiLiturgia(getContext());
                            testoHtml = DataUtils.estraiTestoLetturaGiorno(data, epifaniaFestiva, mostraVersetti, dao, lingua, versioneSinistra);
                            testoPulitoCondivisione = Html.fromHtml(Regex.eliminaVersetti(testoHtml)).toString();
                            testoPulitoLettura = Html.fromHtml(Regex.eliminaTitoliLettureGiorno(Regex.eliminaVersetti(testoHtml))).toString().trim();
                        } catch (Exception e) {
                            testoHtml = getActivity().getString(R.string.riferimentoNonTrovato);
                        }
                    }
                } catch (Exception e) {
                    //
                }
                return null;
            }

            @Override
            protected void onPostExecute(String t) {
                ricaricaWebView();
            }
        };
        task.execute();
    }

    @Override
    protected void visualizzaConfronto() {
        showProgressBar();
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                testoPulitoLettura = null;
                try {
                    Inizializzazione.caricaDatabaseSeNecessario(getActivity(), versioneDestra);
                    Inizializzazione.caricaDatabaseSeNecessario(getActivity(), versioneSinistra);
                    if (terzaVersione != null) {
                        Inizializzazione.caricaDatabaseSeNecessario(getActivity(), terzaVersione);
                    }
                    String lingua = Preferenze.ottieniLingua(getContext());
                    if (letture != null) {
                        try {
                            TestoLettureGiornoConfronto testoLettureGiornoConfronto = DataUtils.estraiConfrontoLetturaRicerca(getContext(), letture, true, dao, versioneSinistra, versioneDestra, terzaVersione, lingua);
                            testoHtml = DataUtils.testoHtmlConfrontaLettureGiorno(testoLettureGiornoConfronto);
                            testoPulitoLettura = Html.fromHtml(Regex.eliminaTitoliLetture(Regex.eliminaVersetti(DataUtils.estraiTestoLetturaRicerca(letture, true, dao, versioneSinistra, lingua)))).toString().trim();
                        } catch (Exception e) {
                            testoHtml = getActivity().getString(R.string.riferimentoNonTrovato);
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            boolean epifaniaFestiva = Preferenze.ottieniEpifaniaFestivaLiturgia(getContext());
                            TestoLettureGiornoConfronto testoLettureGiornoConfronto = DataUtils.estraiConfrontoLetturaGiorno(getContext(), data, epifaniaFestiva, dao, versioneSinistra, versioneDestra, terzaVersione, lingua);
                            testoHtml = DataUtils.testoHtmlConfrontaLettureGiorno(testoLettureGiornoConfronto);
                            testoPulitoLettura = Html.fromHtml(Regex.eliminaTitoliLetture(Regex.eliminaVersetti(DataUtils.estraiTestoLetturaGiorno(data, epifaniaFestiva, false, dao, lingua, versioneSinistra)))).toString().trim();
                        } catch (Exception e) {
                            testoHtml = getActivity().getString(R.string.riferimentoNonTrovato);
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String t) {
                ricaricaWebView();
            }
        };
        task.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (modalitaConfronto) {
            inflater.inflate(R.menu.confronta_lettura, menu);
        } else {
            inflater.inflate(R.menu.lettura, menu);
        }

        MenuItem datButton = menu.findItem(R.id.action_cambia_data);
        if (data == null) {
            datButton.setVisible(false);
        }
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (letture != null) {
                actionBar.setTitle(getString(R.string.ricerca_letture));
                actionBar.setSubtitle(null);
            } else {
                int giorni = Util.getGiorniFraDueDate(Util.getDataCorrente(), data);
                actionBar.setSubtitle(getString(R.string.letture_del_giorno));
                switch (giorni) {
                    case 0:
                        actionBar.setTitle(getString(R.string.oggi));
                        break;
                    case 1:
                        actionBar.setTitle(getString(R.string.domani));
                        break;
                    case -1:
                        actionBar.setTitle(getString(R.string.ieri));
                        break;
                    default:
                        if (giorni > 0) {
                            actionBar.setTitle(getString(R.string.fra_giorni, String.valueOf(giorni)));
                        } else {
                            actionBar.setTitle(getString(R.string.fa_giorni, String.valueOf(-1 * giorni)));
                        }
                        break;
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share: {
                if (testoPulitoCondivisione != null) {
                    String testo = testoPulitoCondivisione;
                    testo += "\n\n" + getString(R.string.inviato_da_app);
                    testo += "\n\nhttps://play.google.com/store/apps/details?id=com.barisi.flavio.bibbiacattolica";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, testo);
                    if (data != null) {
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.letture_di) + Util.formattaDataFull(data, Preferenze.ottieniLingua(getContext())));
                    } else {
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.lettura));
                    }
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                }
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
            case R.id.action_apri_contesto: {
                showDialog2();
                forzaModalitaNotte = true;
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostraMesssaggio(String messaggio) {
        Snackbar snack = Snackbar.make(coordinatorLayoutView, messaggio, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.show();
    }

    private void messaggioAvviso(String titolo, String testo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titolo);
        builder.setMessage(testo);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.show();
    }

    @Override
    public int zoomPlus() {
        int currentZoom = articleView.getSettings().getTextZoom();
        articleView.getSettings().setTextZoom(currentZoom + 10);
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
        if (getParentFragment() != null) {
            ((LeggiAltaVoceFragment) getParentFragment()).updateFragments();
        }
        return zoomDefault;
    }

    @Override
    public void modalitaNotte(int mod) {
        super.modalitaNotte(mod);
        if (getParentFragment() != null) {
            ((LeggiAltaVoceFragment) getParentFragment()).updateFragments();
        }
    }

    @Override
    public void mostraACapo(int modalita) {
        Preferenze.salvaACapoLettureGiorno(getContext(), modalita);
        aCapo = modalita;
        Utility.eseguiJavascript(articleView, "mostraACapo('" + modalita + "');");
        if (getParentFragment() != null) {
            ((LeggiAltaVoceFragment) getParentFragment()).updateFragments();
        }
    }

    @Override
    public void testoGiustificato(int modalita) {
        Preferenze.salvaTestoGiustificatoLettureGiorno(getContext(), modalita);
        testoGiustificato = modalita;
        ricaricaWebView();
        if (getParentFragment() != null) {
            ((LeggiAltaVoceFragment) getParentFragment()).updateFragments();
        }
    }

    private void showDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.scegliLettura));
        final List<Triple<String, String, String>> lista;
        String lingua = Preferenze.ottieniLingua(getContext());
        if (letture != null) {
            lista = DataUtils.estraiElencoCapitoliCodice(letture, dao, lingua);
        } else {
            boolean epifaniaFestiva = Preferenze.ottieniEpifaniaFestivaLiturgia(getContext());
            lista = DataUtils.estraiElencoCapitoliLetturaGiorno(data, epifaniaFestiva, dao, lingua);
        }
        if (lista == null || lista.size() == 0) {
            messaggioAvviso(getString(R.string.errore), getString(R.string.nessunaLetturaDisponibile));
            return;
        }
        List<String> items = new ArrayList<>();
        for (Triple<String, String, String> t : lista) {
            items.add(t.getVal2());
        }
        String[] i = items.toArray(new String[1]);
        builder.setSingleChoiceItems(i, 0, null);
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            Triple<String, String, String> st = lista.get(selectedPosition);
                            ((MainActivity) getActivity()).onArticleFragmentInteraction(st.getVal0(), st.getVal1());
                        } catch (Exception e) {
                            mostraMesssaggio(getString(R.string.errore_des));
                        }
                    }
                }

        );

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }

        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (!Preferenze.seHintVisto("hint_disclaimer_letture", getContext())) {
            Preferenze.settaHintVisto("hint_disclaimer_letture", getContext());
            messaggioAvviso(getString(R.string.attenzione), getString(R.string.disclaimerLetture));
        }*/
        if (forzaModalitaNotte) {
            forzaModalitaNotte = false;
            int zoomDefault = Preferenze.zoomDefault(getContext());
            int mN = Preferenze.ottieniModalitaNotte(getContext());
            aCapo = Preferenze.ottieniACapoLettureGiorno(getContext());
            testoGiustificato = Preferenze.ottieniTestoGiustificatoLettureGiorno(getContext());
            if (articleView != null) {
                articleView.getSettings().setTextZoom(zoomDefault);
                if (mN != modalitaNotte) {
                    modalitaNotte = mN;
                    getActivity().recreate();
                }
                Utility.eseguiJavascript(articleView, "mostraACapo('" + aCapo + "');");
                Utility.eseguiJavascript(articleView, "testoGiustificato('" + testoGiustificato + "');");
            }
        }
    }

    @Override
    public boolean isDisplayingIntroduzione() {
        return false;
    }

    @Override
    public String getTestoPulito() {
        return testoPulitoLettura;
    }


    @Override
    public void update() {
        if (getContext() != null && articleView != null) {
            boolean refresh = false;
            int zoomDefault = Preferenze.zoomDefault(getContext());
            int aC = Preferenze.ottieniACapoLettureGiorno(getContext());
            int tG = Preferenze.ottieniTestoGiustificatoLettureGiorno(getContext());
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

    @Override
    protected View getHintIndicator() {
        return hintIndicator;
    }

    protected void ricaricaWebView() {
        try {
            String stiliAggiuntivi;
            String testoFinito;
            if (modalitaConfronto) {
                stiliAggiuntivi = Css.getColoreStyle(getContext(), modalitaNotte);
                testoFinito = DataUtils.intestazioneHtmlConfronto(stiliAggiuntivi, testoHtml);
            } else {
                stiliAggiuntivi = Css.getColoreStyle(getContext(), modalitaNotte) +
                        Css.getACapoStyle(aCapo) +
                        Css.getTestoGiustificatoStyle(testoGiustificato);
                String linguaBibbia = DatabaseHelper.databaseLanguage(getContext(), getVersioneBibbia());
                boolean hypenate = Regex.stringaNonVuota(linguaBibbia) && testoGiustificato == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                testoFinito = DataUtils.intestazioneHtml(stiliAggiuntivi, testoHtml, false, linguaBibbia, hypenate);
            }
            articleView.loadDataWithBaseURL("file:///android_asset/", testoFinito, "text/html", "UTF-8", null);
            int zoomDefault = Preferenze.zoomDefault(getContext());
            articleView.getSettings().setTextZoom(zoomDefault);
            getActivity().invalidateOptionsMenu();
            fastScroller.setWebView(articleView);
            fastScroller.setVersetti(new ArrayList<String>());
            fastScroller.setAppBar(appBar);
            hideProgressBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
