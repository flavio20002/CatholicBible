package com.barisi.flavio.bibbiacattolica.tasks;

import android.os.AsyncTask;

import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.fragments.MapsFragment;
import com.barisi.flavio.bibbiacattolica.model.Mappa;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MapsTask extends AsyncTask<String, Void, List<Mappa>> {

    private WeakReference<MapsFragment> activityReference;

    public MapsTask(MapsFragment context) {
        activityReference = new WeakReference<>(context);
    }

    @Override
    protected List<Mappa> doInBackground(String... strings) {
        try {
            return new ServiziDatabase(activityReference.get().getContext()).listaMappe();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(List<Mappa> mappe) {
        if (activityReference != null && activityReference.get() != null && activityReference.get().mAdapter != null) {
            activityReference.get().mAdapter.clearItems();
            activityReference.get().mAdapter.addItems(mappe);
            activityReference.get().mAdapter.notifyDataSetChanged();
        }
    }
}
