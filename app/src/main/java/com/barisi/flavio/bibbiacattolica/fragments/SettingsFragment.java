package com.barisi.flavio.bibbiacattolica.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.Cache;
import com.barisi.flavio.bibbiacattolica.Costanti;
import com.barisi.flavio.bibbiacattolica.MainActivity;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.database.Inizializzazione;
import com.thebluealliance.spectrum.SpectrumPreferenceCompat;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * shows the settings option for choosing the movie categories in ListPreference.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        ListPreference bibbie = (ListPreference) findPreference("pref_vers_bibbia");
        bibbie.setEntries(Cache.getBibbieEntries(getContext(), new String[]{
                Costanti.VERSIONE_BIBBA_EBRAICA, Costanti.VERSIONE_BIBBA_EBRAICA_TRASL, Costanti.VERSIONE_BIBBA_GRECA_TRASL}));
        bibbie.setEntryValues(Cache.getBibbieEntriesValues(getContext(), new String[]{
                Costanti.VERSIONE_BIBBA_EBRAICA, Costanti.VERSIONE_BIBBA_EBRAICA_TRASL, Costanti.VERSIONE_BIBBA_GRECA_TRASL}));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Preference myPref = findPreference("pref_reimposta");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.attenzione)
                        .setMessage(R.string.vuoi_ripristinare_impostazioni).setCancelable(true)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Preferenze.reimpostaPreferenze(getContext());
                                try {
                                    Inizializzazione.inizializza(getContext());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, null).create().show();
                return true;
            }
        });

        Preference myPrefInfo = findPreference("pref_info");
        Long freeSpace = Utility.getAvailableInternalMemorySize();
        Long spazioOccupatoCache = Inizializzazione.spazioOccupatoCache(getContext());
        if (freeSpace != null && spazioOccupatoCache != null) {
            Locale loc = Preferenze.ottieniLingua(getContext()).equals("it") ? Locale.ITALIAN : Locale.ENGLISH;
            String freeSpaceString = NumberFormat.getInstance(loc).format(freeSpace);
            String spazioOccupatoCacheString = NumberFormat.getInstance(loc).format(spazioOccupatoCache);
            myPrefInfo.setSummary(String.format(getContext().getString(R.string.info), spazioOccupatoCacheString, freeSpaceString));
        }
        myPrefInfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.attenzione)
                        .setMessage(R.string.vuoi_cancellare_cache).setCancelable(true)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Inizializzazione.eliminaBibbieCache(getContext());
                                    getActivity().recreate();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, null).create().show();
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Fabs
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideFabs();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.empty, menu);
        Activity activity = getActivity();
        if (activity != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getResources().getString(R.string.impostazioni));
                actionBar.setSubtitle(null);
            }
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reimposta_preferenze: {
                Preferenze.reimpostaPreferenze(getContext());
                try {
                    Inizializzazione.caricaDatabaseSeNecessario(getContext(), Preferenze.ottieniVersioneBibbia(getContext()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "pref_vers_bibbia":
                try {
                    Inizializzazione.caricaDatabaseSeNecessario(getContext(), sharedPreferences.getString("pref_vers_bibbia", getResources().getString(R.string.imp_vers_bibbia_default)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "pref_tema":
                String tema = sharedPreferences.getString(key, getResources().getString(R.string.imp_tema_default));
                switch (tema) {
                    case "light":
                        Preferenze.salvaModalitaNotte(getContext(), 0);
                        break;
                    case "dark":
                        Preferenze.salvaModalitaNotte(getContext(), 3);
                        break;
                    case "black":
                        Preferenze.salvaModalitaNotte(getContext(), 1);
                        break;
                }
                getActivity().recreate();
                break;
            case "pref_colore":
                getActivity().recreate();
                break;
            case "pref_nascondi_toolbar":
                getActivity().recreate();
                break;
            case "lingua":
                Cache.clearCache();
                getActivity().recreate();
                break;
            case "pref_mostra_num_capitoli":
                break;
        }
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (!SpectrumPreferenceCompat.onDisplayPreferenceDialog(preference, this)) {
            super.onDisplayPreferenceDialog(preference);
        }
    }

}
