package com.barisi.flavio.bibbiacattolica.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.barisi.flavio.bibbiacattolica.Costanti;
import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.calendario.DataUtils;
import com.barisi.flavio.bibbiacattolica.database.ServiziDatabase;
import com.barisi.flavio.bibbiacattolica.fragments.PrayerViewFragment;
import com.barisi.flavio.bibbiacattolica.gui.Css;

import java.lang.ref.WeakReference;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class PrayerViewTask extends AsyncTask<String, Void, String> {

    private WeakReference<PrayerViewFragment> activityReference;

    public PrayerViewTask(PrayerViewFragment context) {
        activityReference = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        Utility.sfondoWebView(activityReference.get().getContext(), activityReference.get().articleView);
    }

    @Override
    protected String doInBackground(String... strings) {
        int modalitaNotte = Preferenze.ottieniModalitaNotte(activityReference.get().getContext());
        String testoPreghiera = new ServiziDatabase(activityReference.get().getContext()).testoPreghiera(activityReference.get().preghiera.getId(), activityReference.get().lingua);
        return DataUtils.intestazioneHtml(Css.getColoreStyle(activityReference.get().getContext(), modalitaNotte), testoPreghiera, true, null, false);
    }

    @Override
    protected void onPostExecute(String testohtml) {
        activityReference.get().articleView.loadDataWithBaseURL("file:///android_asset/", testohtml, "text/html", "UTF-8", null);
        Log.d("testohtml", testohtml);
        int zoomDefault = Preferenze.zoomDefault(activityReference.get().getContext());
        activityReference.get().articleView.getSettings().setTextZoom(zoomDefault);
        if (activityReference != null && activityReference.get() != null && activityReference.get().getContext() != null && testohtml.contains("class=\"immagine\"") && activityReference.get().hintIndicator != null && !Preferenze.seHintVisto("hint_ingrandisci_immagine", activityReference.get().getContext())) {
            Preferenze.settaHintVisto("hint_ingrandisci_immagine", activityReference.get().getContext());
            new MaterialShowcaseView.Builder(activityReference.get().getActivity())
                    .setTarget(activityReference.get().hintIndicator)
                    .setDismissOnTouch(true)
                    .setMaskColour(Utility.adjustAlpha(Preferenze.ottieniColorePrimario(activityReference.get().getContext()), Costanti.TRASPARENZA_HINT_FAST_SCROLLING))
                    .setDismissText(activityReference.get().getString(R.string.hintVisto))
                    .setContentText(activityReference.get().getString(R.string.hintIngrandisciImmagine))
                    .show();
        }
    }
}
