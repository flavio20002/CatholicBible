package com.barisi.flavio.bibbiacattolica.tasks;

import android.os.AsyncTask;

import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.fragments.PrayersFragment;
import com.barisi.flavio.bibbiacattolica.model.Preghiera;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PrayersTask extends AsyncTask<String, Void, List<Preghiera>> {

    private WeakReference<PrayersFragment> activityReference;
    private String query;

    public PrayersTask(PrayersFragment context, String query) {
        activityReference = new WeakReference<>(context);
        this.query = query;
    }

    @Override
    protected List<Preghiera> doInBackground(String... strings) {
        try {
            return new ServiziDatabase(activityReference.get().getContext()).listaPreghiere(query);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(List<Preghiera> preghiere) {
        activityReference.get().mAdapter.clearItems();
        activityReference.get().mAdapter.addItems(preghiere);
        activityReference.get().mAdapter.notifyDataSetChanged();
    }
}
