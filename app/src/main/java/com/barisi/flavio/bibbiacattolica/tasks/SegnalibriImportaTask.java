package com.barisi.flavio.bibbiacattolica.tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.barisi.flavio.bibbiacattolica.ContentUtility;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.fragments.SegnalibriFragment;
import com.barisi.flavio.bibbiacattolica.model.Segnalibri;
import com.barisi.flavio.bibbiacattolica.model.Segnalibro;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.List;

public class SegnalibriImportaTask extends AsyncTask<String, Void, String> {

    private final Intent dataToImport;
    private final List<Segnalibro> segnalibriDaConservare;
    private Segnalibri listaLetta;
    private WeakReference<SegnalibriFragment> activityReference;

    public SegnalibriImportaTask(SegnalibriFragment context, Intent dataToImport, List<Segnalibro> segnalibriDaConservare) {
        activityReference = new WeakReference<>(context);
        this.dataToImport = dataToImport;
        this.segnalibriDaConservare = segnalibriDaConservare;
    }

    @Override
    protected void onPreExecute() {
        SegnalibriFragment reference = activityReference != null ? activityReference.get() : null;
        Context context = reference != null ? reference.getContext() : null;
        ContentResolver contentResolver = context != null ? context.getContentResolver() : null;
        try {
            if (contentResolver != null && dataToImport != null && dataToImport.getData() != null) {
                activityReference.get().showProgressBar(true);
                String contenuto = ContentUtility.convertStreamToString(contentResolver.openInputStream(dataToImport.getData()));
                Gson gson = new Gson();
                listaLetta = gson.fromJson(contenuto, Segnalibri.class);
            }
        } catch (Exception e) {
            reference.hideProgressBar();
            Utility.messaggioAvviso(reference.getContext(), R.string.errore, R.string.errore_des, null);
            e.printStackTrace();
            listaLetta = null;
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        SegnalibriFragment reference = activityReference != null ? activityReference.get() : null;
        if (listaLetta != null && reference != null) {
            try {
                reference.serviziDatabase.cancellareVersettiPreferiti();
                for (Segnalibro s : listaLetta) {
                    reference.serviziDatabase.salvaVersettoPreferito(s.getIdCapitolo(), s.getVersetto(), s.getNota());
                }
                if (segnalibriDaConservare != null) {
                    for (Segnalibro s : segnalibriDaConservare) {
                        reference.serviziDatabase.salvaVersettoPreferito(s.getIdCapitolo(), s.getVersetto(), s.getNota());
                    }
                }
            } catch (Exception e) {
                reference.hideProgressBar();
                Utility.messaggioAvviso(reference.getContext(), R.string.errore, R.string.errore_des, null);
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String testohtml) {
        SegnalibriFragment reference = activityReference != null ? activityReference.get() : null;
        if (reference != null) {
            reference.refreshItems();
        }
    }
}
