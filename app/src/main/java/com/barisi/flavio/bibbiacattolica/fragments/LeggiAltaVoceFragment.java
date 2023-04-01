package com.barisi.flavio.bibbiacattolica.fragments;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.database.DatabaseHelper;
import com.barisi.flavio.bibbiacattolica.gui.MyPagerAdapter;
import com.barisi.flavio.bibbiacattolica.interfaces.BibbiaSelectedListener;

import java.util.HashMap;
import java.util.Locale;


@SuppressWarnings({"WrongConstant", "deprecation"})
public abstract class LeggiAltaVoceFragment extends Fragment {

    private static final String STATE_LEGGI = "StatoLeggi";

    private FloatingActionButton fab, fab2;
    private TextToSpeech tts;
    private boolean isSpeaking;
    private String[] testoDaLeggere;
    private int indiceLettura;
    private static final String STATE_MODALITACONFRONTO = "modalitaConfronto";
    private static final String STATE_VERSIONE_SINISTRA = "versioneSinistra";
    private static final String STATE_VERSIONE_DESTRA = "versioneDestra";
    private static final String STATE_TERZA_VERSIONE = "terzaVersione";

    public boolean modalitaConfronto;
    public String versioneSinistra, versioneDestra, terzaVersione;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            modalitaConfronto = savedInstanceState.getBoolean(STATE_MODALITACONFRONTO);
            versioneSinistra = savedInstanceState.getString(STATE_VERSIONE_SINISTRA);
            versioneDestra = savedInstanceState.getString(STATE_VERSIONE_DESTRA);
            terzaVersione = savedInstanceState.getString(STATE_TERZA_VERSIONE);
        } else {
            modalitaConfronto = false;
            versioneSinistra = Preferenze.ottieniVersioneBibbia(getContext());
            versioneDestra = null;
            terzaVersione = null;
        }
        setHasOptionsMenu(true);
    }

    void inizializza() {
        if (getActivity() != null) {
            fab = getActivity().findViewById(R.id.fab1);
            fab2 = getActivity().findViewById(R.id.fab2);
            fab.setVisibility(FloatingActionButton.GONE);
            fab2.setVisibility(FloatingActionButton.GONE);
            isSpeaking = false;
            indiceLettura = 0;
            testoDaLeggere = null;

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attivaDisattivaLeggi();
                }
            });
            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    indiceLettura = 0;
                    aggiornaFab(false);
                }
            });
        }
    }


    private boolean impostaLinguaTts() {
        tts.setSpeechRate(Preferenze.ottieniVelocitàLettura(getContext()));
        boolean isIntroduzione = getPagerAdapter().getmCurrentFragment().isDisplayingIntroduzione();
        String lingua = Preferenze.ottieniLingua(getContext());
        String linguaBibbia = DatabaseHelper.databaseLanguage(getContext(), getPagerAdapter().getmCurrentFragment().getVersioneBibbia());
        if (isIntroduzione) {
            tts.setLanguage(lingua.equals("it") ? Locale.ITALY : Locale.US);
        } else {
            if (Regex.stringaVuota(linguaBibbia)) {
                //Lettura non disponibile
                return false;
            } else {
                tts.setLanguage(linguaBibbia.equals("it") ? Locale.ITALY : Locale.US);
            }
        }
        return true;
    }

    abstract MyPagerAdapter getPagerAdapter();


    private void attivaDisattivaLeggi() {
        if (tts != null) {
            if (isSpeaking) {
                tts.stop();
                isSpeaking = false;
                aggiornaFab(false);
                if (getActivity() != null && !Preferenze.ottieniSchermoAcceso(getContext()))
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                if (!impostaLinguaTts()) {
                    Utility.mostraSnackBar(getContext(), getPagerAdapter().getmCurrentFragment().getCoordinatorLayoutView(), getString(R.string.lettura_alta_voce_non_disponibile));
                    return;
                }
                if (testoDaLeggere == null) {
                    String testo = getPagerAdapter().getmCurrentFragment().getTestoPulito();
                    if (testo != null) {
                        testoDaLeggere = testo.split("[.?!]");
                        indiceLettura = 0;
                    }
                }
                if (getActivity() != null && tts != null && testoDaLeggere != null && testoDaLeggere.length > 0) {
                    HashMap<String, String> map = new HashMap<>();
                    isSpeaking = true;
                    aggiornaFab(true);
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    if (indiceLettura >= testoDaLeggere.length - 1) {
                        indiceLettura = 0;
                    }
                    for (int i = indiceLettura; i < testoDaLeggere.length; i++) {
                        if (i == indiceLettura) {
                            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(i));
                            tts.speak(testoDaLeggere[i].trim(), TextToSpeech.QUEUE_FLUSH, map);
                        } else {
                            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(i));
                            tts.speak(testoDaLeggere[i].trim(), TextToSpeech.QUEUE_ADD, map);
                        }
                        tts.playSilence(300, TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }
        } else {
            isSpeaking = false;
            aggiornaFab(false);
            if (getActivity() != null && !Preferenze.ottieniSchermoAcceso(getContext()))
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }


    public void aggiornaFab(boolean isSpeaking) {
        if (getContext() != null) {
            fab2.setVisibility(fab.getVisibility() == FloatingActionButton.GONE || isSpeaking || indiceLettura == 0 ? FloatingActionButton.GONE : FloatingActionButton.VISIBLE);
            fab.setImageResource(isSpeaking ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_white_24dp);
            coloreFab();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_LEGGI, fab.getVisibility() == View.VISIBLE);
        savedInstanceState.putBoolean(STATE_MODALITACONFRONTO, modalitaConfronto);
        savedInstanceState.putString(STATE_VERSIONE_DESTRA, versioneDestra);
        savedInstanceState.putString(STATE_VERSIONE_SINISTRA, versioneSinistra);
        savedInstanceState.putString(STATE_TERZA_VERSIONE, terzaVersione);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.empty, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem leggiButton = menu.findItem(R.id.action_leggi_alta_voce);
        if (leggiButton != null && fab != null) {
            leggiButton.setTitle(fab.getVisibility() == WebView.VISIBLE ? R.string.smetti_leggi_alta_voce : R.string.leggi_alta_voce);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_leggi_alta_voce: {
                if (fab.getVisibility() == FloatingActionButton.VISIBLE) {
                    interrompiLettura();
                    indiceLettura = 0;
                    testoDaLeggere = null;
                    fab.setVisibility(FloatingActionButton.GONE);
                    fab2.setVisibility(FloatingActionButton.GONE);
                    Preferenze.salvaLeggiAltaVoce(getContext(), false);
                } else {
                    mostraFabSePresenteSintetizzatore();
                    Preferenze.salvaLeggiAltaVoce(getContext(), true);
                }
                if (getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            }
            case R.id.action_cambia_versione_sinistra: {
                Utility.mostraDialogoVersioniBibbia(getContext(), versioneSinistra, new String[]{versioneDestra, terzaVersione}, new BibbiaSelectedListener() {
                    @Override
                    public void bibbiaSelected(String version) {
                        try {
                            invalidaTestoDaLeggere();
                            versioneSinistra = version;
                            updateFragments();
                        } catch (Exception e) {
                            Utility.messaggioAvviso(getContext(), R.string.errore, R.string.errore_des, null);
                        }
                    }
                });
                return true;
            }
            case R.id.action_cambia_versione_destra: {
                Utility.mostraDialogoVersioniBibbia(getContext(), versioneDestra, new String[]{versioneSinistra, terzaVersione}, new BibbiaSelectedListener() {
                    @Override
                    public void bibbiaSelected(String version) {
                        try {
                            versioneDestra = version;
                            updateFragments();
                        } catch (Exception e) {
                            Utility.messaggioAvviso(getContext(), R.string.errore, R.string.errore_des, null);
                        }
                    }
                });
                return true;
            }
            case R.id.action_aggiungi_terza_versione: {
                if (terzaVersione == null) {
                    Utility.mostraDialogoVersioniBibbia(getContext(), null, new String[]{versioneSinistra, versioneDestra}, new BibbiaSelectedListener() {
                        @Override
                        public void bibbiaSelected(String version) {
                            try {
                                terzaVersione = version;
                                updateFragments();
                            } catch (Exception e) {
                                Utility.messaggioAvviso(getContext(), R.string.errore, R.string.errore_des, null);
                            }
                        }
                    });
                } else {
                    try {
                        terzaVersione = null;
                        updateFragments();
                    } catch (Exception e) {
                        Utility.messaggioAvviso(getContext(), R.string.errore, R.string.errore_des, null);
                    }
                }
                return true;
            }
            case R.id.action_confronta: {
                Utility.mostraDialogoVersioniBibbia(getContext(), null, new String[]{versioneSinistra}, new BibbiaSelectedListener() {
                    @Override
                    public void bibbiaSelected(String version) {
                        try {
                            versioneDestra = version;
                            terzaVersione = null;
                            modalitaConfronto = true;
                            updateFragments();
                        } catch (Exception e) {
                            Utility.messaggioAvviso(getContext(), R.string.errore, R.string.errore_des, null);
                        }
                    }
                });
                return true;
            }
            case R.id.action_smetti_confronto: {
                chiudiConfronto();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostraFabSePresenteSintetizzatore() {
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (getContext() != null) {
                    if (status != TextToSpeech.ERROR) {
                        fab.show();
                        aggiornaFab(isSpeaking);
                        tts.setOnUtteranceProgressListener(new MyUtteranceProgressListener());
                        if (!Preferenze.seHintVisto("hint_sintesi_vocale", getContext())) {
                            Preferenze.settaHintVisto("hint_sintesi_vocale", getContext());
                            Utility.messaggioAvviso(getContext(), R.string.attenzione, R.string.attenzione_sintesi_vocale, null);
                        }
                    } else {
                        Utility.mostraSnackBar(getContext(), getPagerAdapter().getmCurrentFragment().getCoordinatorLayoutView(), getString(R.string.sintetizzatore_non_disponibile));
                    }
                }
            }
        });
    }


    private void chiudiConfronto() {
        modalitaConfronto = false;
        versioneDestra = null;
        terzaVersione = null;
        updateFragments();
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            Boolean mostraLeggiAltaVoce = Preferenze.ottieniLeggiAltaVoce(getContext());
            if (mostraLeggiAltaVoce) {
                mostraFabSePresenteSintetizzatore();
            } else {
                fab.setVisibility(FloatingActionButton.GONE);
            }
        } catch (Exception e) {
            //
        }
        if (tts != null) {
            aggiornaFab(isSpeaking);
        }
        if (getActivity() != null && Preferenze.ottieniSchermoAcceso(getContext())) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onPause() {
        interrompiLettura();
        super.onPause();
    }

    private void interrompiLettura() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        isSpeaking = false;
        if (getActivity() != null && !Preferenze.ottieniSchermoAcceso(getContext()))
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    public void coloreFab() {
        int modalitaNotte = Preferenze.ottieniModalitaNotte(getContext());
        if (getActivity() != null && getContext() != null) {
            FloatingActionButton fab = getActivity().findViewById(R.id.fab1);
            FloatingActionButton fab2 = getActivity().findViewById(R.id.fab2);
            int bianco = ContextCompat.getColor(getContext(), R.color.bianco);
            int nero = ContextCompat.getColor(getContext(), R.color.nero);
            if (modalitaNotte == 0) {
                fab.setBackgroundTintList(ColorStateList.valueOf(Preferenze.colorePrincipale(getContext())));
                fab2.setBackgroundTintList(ColorStateList.valueOf(Preferenze.colorePrincipale(getContext())));
                fab.getDrawable().setColorFilter(bianco, PorterDuff.Mode.SRC_IN);
                fab2.getDrawable().setColorFilter(bianco, PorterDuff.Mode.SRC_IN);
            } else if (modalitaNotte == 1) {
                fab.setBackgroundTintList(ColorStateList.valueOf(Preferenze.coloreSecondario(getContext())));
                fab2.setBackgroundTintList(ColorStateList.valueOf(Preferenze.coloreSecondario(getContext())));
                fab.getDrawable().setColorFilter(nero, PorterDuff.Mode.SRC_IN);
                fab2.getDrawable().setColorFilter(nero, PorterDuff.Mode.SRC_IN);
            } else if (modalitaNotte == 2) {
                fab.setBackgroundTintList(ColorStateList.valueOf(Preferenze.colorePrincipale(getContext())));
                fab2.setBackgroundTintList(ColorStateList.valueOf(Preferenze.colorePrincipale(getContext())));
                fab.getDrawable().setColorFilter(bianco, PorterDuff.Mode.SRC_IN);
                fab2.getDrawable().setColorFilter(bianco, PorterDuff.Mode.SRC_IN);
            } else if (modalitaNotte == 3) {
                fab.setBackgroundTintList(ColorStateList.valueOf(Preferenze.coloreSecondario(getContext())));
                fab2.setBackgroundTintList(ColorStateList.valueOf(Preferenze.coloreSecondario(getContext())));
                fab.getDrawable().setColorFilter(nero, PorterDuff.Mode.SRC_IN);
                fab2.getDrawable().setColorFilter(nero, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private class MyUtteranceProgressListener extends android.speech.tts.UtteranceProgressListener {

        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            Log.i("onDone", "utteranceId=" + utteranceId + " and lenght =" + testoDaLeggere.length);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                indiceLettura = Integer.valueOf(utteranceId);
            } else {
                indiceLettura = Integer.valueOf(utteranceId) + 1;
            }
            if (getActivity() != null && testoDaLeggere != null && indiceLettura >= testoDaLeggere.length) {
                Log.i("onDone", "Esco");
                indiceLettura = 0;
                isSpeaking = false;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("onDone", "Aggiorno Fab");
                        aggiornaFab(false);
                        if (!Preferenze.ottieniSchermoAcceso(getContext()))
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                });
            }
        }

        @Override
        public void onError(String utteranceId) {
        }
    }

    void cambiaPagina() {
        testoDaLeggere = null;
        indiceLettura = 0;
        aggiornaFab(false);
        if (tts != null && isSpeaking) {
            tts.stop();
            isSpeaking = false;
            if (getActivity() != null && !Preferenze.ottieniSchermoAcceso(getContext()))
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            attivaDisattivaLeggi();
        }
    }


    public void invalidaTestoDaLeggere() {
        testoDaLeggere = null;
        indiceLettura = 0;
        interrompiLettura();
        aggiornaFab(false);
    }

    public void updateFragments() {
        getMPagerAdapter().notifyDataSetChanged();
    }

    abstract MyPagerAdapter getMPagerAdapter();

}
