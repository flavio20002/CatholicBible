package com.barisi.flavio.bibbiacattolica.fragments;


import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.barisi.flavio.bibbiacattolica.Costanti;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.gui.NestedWebView;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.VerticalWebViewFastScroller;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.SectionTitleIndicator;
import com.rey.material.widget.ProgressView;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

@SuppressWarnings("deprecation")
public abstract class LeggiFragment extends Fragment {

    private static final String STATE_MODALITACONFRONTO = "modalitaConfronto";
    private static final String STATE_VERSIONE_SINISTRA = "versioneSinistra";
    private static final String STATE_VERSIONE_DESTRA = "versioneDestra";
    private static final String STATE_TERZA_VERSIONE = "terzaVersione";

    ServiziDatabase serviziDatabase;
    CoordinatorLayout coordinatorLayoutView;
    AppBarLayout appBar;
    SectionTitleIndicator sectionTitleIndicator;
    private boolean fab1HiddenbyScroll, fab2HiddenbyScroll;

    String versioneSinistra, versioneDestra, terzaVersione;
    boolean modalitaConfronto = false;
    int modalitaNotte;
    NestedWebView articleView;
    VerticalWebViewFastScroller fastScroller;
    View hintIndicator;
    ProgressView circularProgressBar;
    int aCapo, testoGiustificato;

    //private MyTimingLogger logger;

    public LeggiFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviziDatabase = new ServiziDatabase(getActivity());
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            modalitaConfronto = savedInstanceState.getBoolean(STATE_MODALITACONFRONTO);
            versioneSinistra = savedInstanceState.getString(STATE_VERSIONE_SINISTRA);
            versioneDestra = savedInstanceState.getString(STATE_VERSIONE_DESTRA);
            terzaVersione = savedInstanceState.getString(STATE_TERZA_VERSIONE);
        } else {
            modalitaConfronto = ((LeggiAltaVoceFragment) getParentFragment()).modalitaConfronto;
            versioneDestra = ((LeggiAltaVoceFragment) getParentFragment()).versioneDestra;
            versioneSinistra = ((LeggiAltaVoceFragment) getParentFragment()).versioneSinistra;
            terzaVersione = ((LeggiAltaVoceFragment) getParentFragment()).terzaVersione;
        }
    }


    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_letture, container, false);
        modalitaNotte = Preferenze.ottieniModalitaNotte(getContext());
        Utility.impostaLingua(getContext());

        articleView = view.findViewById(R.id.article_text);
        sfondoWebView(articleView);
        articleView.getSettings().setBlockNetworkLoads(true);
        articleView.getSettings().setJavaScriptEnabled(true);
        articleView.getSettings().setAppCacheEnabled(false);
        articleView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        Boolean accellerazioneHW = Preferenze.ottieniAccellerazioneHardware(getContext());
        if (!accellerazioneHW) {
            articleView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        articleView.setOnScrollChangedCallback(new NestedWebView.OnScrollChangedCallback() {
            @Override
            public void onScrollChange(WebView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (getActivity() != null) {
                    FloatingActionButton fab = getActivity().findViewById(R.id.fab1);
                    FloatingActionButton fab2 = getActivity().findViewById(R.id.fab2);
                    if (scrollY > oldScrollY && scrollY > 0) {
                        if (fab != null && fab.getVisibility() == View.VISIBLE) {
                            fab.hide();
                            fab1HiddenbyScroll = true;
                        }
                        if (fab2 != null && fab2.getVisibility() == View.VISIBLE) {
                            fab2.hide();
                            fab2HiddenbyScroll = true;
                        }
                    }
                    if (scrollY < oldScrollY) {
                        if (fab1HiddenbyScroll && fab != null && fab.getVisibility() != View.VISIBLE) {
                            fab.show();
                            fab1HiddenbyScroll = false;
                        }
                        if (fab2HiddenbyScroll && fab2 != null && fab2.getVisibility() != View.VISIBLE) {
                            fab2.show();
                            fab2HiddenbyScroll = false;
                        }
                    }
                }

            }
        });

        coordinatorLayoutView = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);
        appBar = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
        fastScroller = (VerticalWebViewFastScroller) view.findViewById(R.id.fast_scroller);
        sectionTitleIndicator = (SectionTitleIndicator) view.findViewById(R.id.fast_scroller_section_title_indicator);
        hintIndicator = view.findViewById(R.id.hint);
        circularProgressBar = (ProgressView) view.findViewById(R.id.circular_progress);
        circularProgressBar.setVisibility(ProgressView.GONE);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_MODALITACONFRONTO, modalitaConfronto);
        savedInstanceState.putString(STATE_VERSIONE_DESTRA, versioneDestra);
        savedInstanceState.putString(STATE_VERSIONE_SINISTRA, versioneSinistra);
        savedInstanceState.putString(STATE_TERZA_VERSIONE, terzaVersione);
        super.onSaveInstanceState(savedInstanceState);
    }

    void visualizza() {
        if (modalitaConfronto && !isDisplayingIntroduzione()) {
            visualizzaConfronto();
        } else {
            visualizzaTestoSingolo();
        }
    }

    void sfondoWebView(WebView vW) {
        Utility.sfondoWebView(getContext(), vW);
        if (getParentFragment() != null) {
            ((LeggiAltaVoceFragment) getParentFragment()).coloreFab();
            if (getParentFragment() instanceof LeggiCapitoloParentFragment) {
                ((LeggiCapitoloParentFragment) getParentFragment()).coloreNoteSplitter();
            }
        }
    }

    void showProgressBar() {
        articleView.loadDataWithBaseURL("file:///android_asset/", "", "text/html", "UTF-8", null);
        circularProgressBar.setVisibility(ProgressView.VISIBLE);
        circularProgressBar.bringToFront();
    }

    void hideProgressBar() {
        circularProgressBar.setVisibility(ProgressView.GONE);
    }

    protected abstract void visualizzaTestoSingolo();

    protected abstract void visualizzaConfronto();

    protected abstract View getHintIndicator();

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem terzaButton = menu.findItem(R.id.action_aggiungi_terza_versione);
        if (terzaButton != null) {
            terzaButton.setTitle(terzaVersione == null ? R.string.aggiungi_terza_versione : R.string.rimuovi_terza_versione);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && sectionTitleIndicator != null) {
            sectionTitleIndicator.animateAlpha(0f);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (sectionTitleIndicator != null) {
            sectionTitleIndicator.animateAlpha(0f);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getHintIndicator() != null && !Preferenze.seHintVisto("hint_fast_scrolling", getContext())) {
            Preferenze.settaHintVisto("hint_fast_scrolling", getContext());
            new MaterialShowcaseView.Builder(getActivity())
                    .setTarget(getHintIndicator())
                    .setDismissOnTouch(true)
                    .setMaskColour(Utility.adjustAlpha(Preferenze.ottieniColorePrimario(getContext()), Costanti.TRASPARENZA_HINT_FAST_SCROLLING))
                    .setDismissText(getString(R.string.hintVisto))
                    .setContentText(getString(R.string.hintFastScrolling))
                    .show();
        }
    }

    public String getVersioneBibbia() {
        return versioneSinistra;
    }

    public CoordinatorLayout getCoordinatorLayoutView() {
        return coordinatorLayoutView;
    }

    public void modalitaNotte(int mod) {
        Preferenze.salvaModalitaNotte(getContext(), mod);
        modalitaNotte = mod;
        sfondoWebView(articleView);
        ricaricaWebView();
    }

    public abstract boolean isDisplayingIntroduzione();

    protected abstract void ricaricaWebView();

    public abstract String getTestoPulito();

}
