package com.barisi.flavio.bibbiacattolica.gui;


import android.content.Context;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import android.util.AttributeSet;

import com.barisi.flavio.bibbiacattolica.Costanti;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.database.DatabaseHelper;
import com.barisi.flavio.bibbiacattolica.database.Inizializzazione;


public class BibbiaListPreference extends ListPreference {
    public BibbiaListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        settaListener();

    }

    public BibbiaListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        settaListener();
    }

    public BibbiaListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        settaListener();
    }

    public BibbiaListPreference(Context context) {
        super(context);
        settaListener();
    }

    private void settaListener() {
        this.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    String espansione = DatabaseHelper.databaseAddOn(getContext(), (String) newValue);
                    int minVersioneEspansione = DatabaseHelper.databaseMinVersioneEspansione(getContext(), (String) newValue);
                    if (Regex.stringaNonVuota(espansione)) {
                        int espansioneInstalled = Inizializzazione.isAppInstalled(getContext(), espansione, minVersioneEspansione);
                        if (espansioneInstalled == Costanti.ESPANSIONE_MANCANTE) {
                            Utility.mostraMessaggioAvvisoEspansione(getContext(), espansione, false);
                            return false;
                        } else if (espansioneInstalled == Costanti.ESPANSIONE_NON_AGGIORNATA) {
                            Utility.mostraMessaggioAvvisoEspansione(getContext(), espansione, true);
                            return false;
                        }
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

}
