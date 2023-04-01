package com.barisi.flavio.bibbiacattolica.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnZoomPressed;

public class ModificaFontAdapter extends ModificaFontLightAdapter {

    private final int aCapo;
    private final int testoGiustificato;

    public ModificaFontAdapter(Context c, int zoomIniziale, int testoGiustificato, int aCapo, OnZoomPressed listener) {
        super(c, zoomIniziale, listener);
        this.aCapo = aCapo;
        this.testoGiustificato = testoGiustificato;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return "Zoom";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View v;
        if (position == 0 || position == 1) {
            return super.getView(position, view, parent);
        } else if (position == 2) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.elemento_lista_format_giustificato, null);
            final Spinner spinner =
                    (Spinner) v.findViewById(R.id.spinner);
            spinner.setSelection(testoGiustificato);
            final boolean[] isSpinnerInitial = {true};
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (isSpinnerInitial[0]) {
                        isSpinnerInitial[0] = false;
                    } else {
                        listener.testoGiustificato(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            return v;
        } else {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.elemento_lista_format_capo, null);
            final Spinner spinner =
                    (Spinner) v.findViewById(R.id.spinner);
            spinner.setSelection(aCapo);
            final boolean[] isSpinnerInitial = {true};
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (isSpinnerInitial[0]) {
                        isSpinnerInitial[0] = false;
                    } else {
                        listener.mostraACapo(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            return v;
        }
    }
}