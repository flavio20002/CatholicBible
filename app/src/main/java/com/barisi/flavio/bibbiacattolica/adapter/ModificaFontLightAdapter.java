package com.barisi.flavio.bibbiacattolica.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnZoomPressed;

public class ModificaFontLightAdapter extends BaseAdapter {

    private final int zoomIniziale;
    final OnZoomPressed listener;
    Context mContext;

    public ModificaFontLightAdapter(Context c, int zoomIniziale, OnZoomPressed listener) {
        this.mContext = c;
        this.zoomIniziale = zoomIniziale;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return 2;
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
        if (position == 0) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.elemento_lista_format_testo_notte, null);
            final Button giorno =
                    (Button) v.findViewById(R.id.button_giorno);
            giorno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.modalitaNotte(0);
                }
            });
            final Button notte =
                    (Button) v.findViewById(R.id.button_notte);
            notte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.modalitaNotte(1);
                }
            });
            final Button giallo =
                    (Button) v.findViewById(R.id.button_giallo);
            giallo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.modalitaNotte(2);
                }
            });
            final Button scuro =
                    (Button) v.findViewById(R.id.button_scuro);
            scuro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.modalitaNotte(3);
                }
            });
            return v;
        } else {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.elemento_lista_format_testo, null);
            final TextView zoom =
                    (TextView) v.findViewById(R.id.text_zoom);
            zoom.setText(String.valueOf(zoomIniziale) + "%");
            final Button minus =
                    (Button) v.findViewById(R.id.button_zoom_minus);
            final Button plus =
                    (Button) v.findViewById(R.id.button_zoom_plus);

            if (zoomIniziale <= 50) {
                minus.setEnabled(false);
            }
            if (zoomIniziale >= 250) {
                plus.setEnabled(false);
            }

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newZoom = listener.zoomMinus();
                    zoom.setText(String.valueOf(newZoom) + "%");
                    if (newZoom <= 50) {
                        minus.setEnabled(false);
                    }
                    if (newZoom < 250) {
                        plus.setEnabled(true);
                    }
                }
            });
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newZoom = listener.zoomPlus();
                    zoom.setText(String.valueOf(newZoom) + "%");
                    if (newZoom > 50) {
                        minus.setEnabled(true);
                    }
                    if (newZoom >= 250) {
                        plus.setEnabled(false);
                    }
                }
            });

            zoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newZoom = listener.zoomReset();
                    zoom.setText(String.valueOf(newZoom) + "%");
                    if (newZoom > 50) {
                        minus.setEnabled(true);
                    }
                    if (newZoom >= 250) {
                        plus.setEnabled(false);
                    }
                }
            });
            return v;
        }
    }
}