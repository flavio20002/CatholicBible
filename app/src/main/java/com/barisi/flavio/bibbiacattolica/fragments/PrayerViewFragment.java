package com.barisi.flavio.bibbiacattolica.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListAdapter;

import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.tasks.PrayerViewTask;
import com.barisi.flavio.bibbiacattolica.Utility;
import com.barisi.flavio.bibbiacattolica.adapter.ModificaFontLightAdapter;
import com.barisi.flavio.bibbiacattolica.interfaces.OnCambiaLinguaListener;
import com.barisi.flavio.bibbiacattolica.interfaces.OnZoomPressed;
import com.barisi.flavio.bibbiacattolica.model.Preghiera;

public class PrayerViewFragment extends Fragment implements OnCambiaLinguaListener, OnZoomPressed {

    public View hintIndicator;
    public Preghiera preghiera;
    public WebView articleView;
    public String lingua;

    public PrayerViewFragment() {
    }

    public static PrayerViewFragment newInstance(Preghiera preghiera) {
        PrayerViewFragment fragment = new PrayerViewFragment();
        Bundle args = new Bundle();
        args.putSerializable("preghiera", preghiera);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            preghiera = (Preghiera) getArguments().getSerializable("preghiera");
        }
        setHasOptionsMenu(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_prayer, container, false);
        articleView = view.findViewById(R.id.web_view);
        articleView.getSettings().setBlockNetworkLoads(true);
        articleView.getSettings().setJavaScriptEnabled(true);
        articleView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        Boolean accellerazioneHW = Preferenze.ottieniAccellerazioneHardware(getContext());
        if (!accellerazioneHW) {
            articleView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        lingua = Preferenze.ottieniLinguaPreghiere(getContext());

        //Hint
        hintIndicator = view.findViewById(R.id.hint);

        visualizzaPreghiera();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.preghiera, menu);
        Utility.aggiornaTitoloActionBar((AppCompatActivity) getActivity(), preghiera.getNomepreghiera(), null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cambia_lingua) {
            Utility.mostraDialogoLinguePreghiere(getContext(), lingua, this);
            return true;
        } else if (getActivity() != null && item.getItemId() == R.id.action_font) {
            View b = getActivity().findViewById(R.id.action_font);
            if (getContext() != null && b != null) {
                ListPopupWindow pop = new ListPopupWindow(getContext());
                ListAdapter ad = new ModificaFontLightAdapter(getContext(), articleView.getSettings().getTextZoom(), this);
                pop.setAnchorView(b);
                pop.setAdapter(ad);
                pop.setModal(true);
                pop.setContentWidth(getResources().getDimensionPixelSize(R.dimen.popup));
                pop.show();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected void visualizzaPreghiera() {
        new PrayerViewTask(this).execute();
    }

    @Override
    public void cambiaLingua(String lingua) {
        this.lingua = lingua;
        Preferenze.salvaLinguaPreghiere(getContext(), lingua);
        visualizzaPreghiera();
    }

    @Override
    public int zoomPlus() {
        int currentZoom = articleView.getSettings().getTextZoom();
        articleView.getSettings().setTextZoom(currentZoom + 10);
        int nuovoZoom = articleView.getSettings().getTextZoom();
        Preferenze.salvaZoom(getContext(), nuovoZoom);
        return nuovoZoom;
    }

    @Override
    public int zoomMinus() {
        int currentZoom = articleView.getSettings().getTextZoom();
        articleView.getSettings().setTextZoom(currentZoom - 10);
        int nuovoZoom = articleView.getSettings().getTextZoom();
        Preferenze.salvaZoom(getContext(), nuovoZoom);
        return nuovoZoom;
    }

    @Override
    public int zoomReset() {
        Preferenze.resetZoom(getContext());
        int zoomDefault = Preferenze.zoomDefault(getContext());
        articleView.getSettings().setTextZoom(zoomDefault);
        return zoomDefault;
    }

    @Override
    public void modalitaNotte(int mod) {
        Preferenze.salvaModalitaNotte(getContext(), mod);
        visualizzaPreghiera();
    }

    @Override
    public void mostraACapo(int modalita) {

    }

    @Override
    public void testoGiustificato(int modalita) {

    }

}
