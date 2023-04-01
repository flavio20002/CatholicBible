package com.barisi.flavio.bibbiacattolica.tasks;

import android.os.AsyncTask;

import com.barisi.flavio.bibbiacattolica.LoadingActivity;
import com.barisi.flavio.bibbiacattolica.database.Inizializzazione;

import java.lang.ref.WeakReference;

public class MainTask extends AsyncTask<String, Void, Void> {

    private WeakReference<LoadingActivity> activityReference;
    private Exception exceptionToBeThrown = null;

    public MainTask(LoadingActivity context) {
        activityReference = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activityReference.get().showLoginLoadingScreen();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            Inizializzazione.inizializza(activityReference.get());
            Thread.sleep(1500);
        } catch (Exception e) {
            exceptionToBeThrown = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (exceptionToBeThrown != null) {
            activityReference.get().showErrorScreen();
        } else {
            activityReference.get().stopAnimation();
            activityReference.get().startMainActivity();
        }
    }
}
