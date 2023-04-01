package com.barisi.flavio.bibbiacattolica.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;
import com.barisi.flavio.bibbiacattolica.model.Libro;
import com.barisi.flavio.bibbiacattolica.model.Segnalibro;

import java.util.List;

public class ListaPreferitiHomeAdapter extends BaseAdapter {

    private Context mContext;
    private List<Segnalibro> capitoli;

    public ListaPreferitiHomeAdapter(Context c, List<Segnalibro> capitoli) {
        this.capitoli = capitoli;
        this.mContext = c;
    }

    @Override
    public int getCount() {
        return capitoli.size();
    }

    @Override
    public Segnalibro getItem(int position) {
        return capitoli.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final Segnalibro i = capitoli.get(position);
        if (i != null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.elemento_lista_preferiti_home, null);
            final TextView riferimento =
                    (TextView) v.findViewById(R.id.riferimento);
            riferimento.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            if (riferimento != null) riferimento.setText(i.getRiferimento());
            final TextView testoBreve =
                    (TextView) v.findViewById(R.id.testo_breve);
            if (i.getTestoBreve() != null && !i.getTestoBreve().equals("")) {
                testoBreve.setVisibility(View.VISIBLE);
                testoBreve.setText(i.getTestoBreve());
                testoBreve.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else {
                testoBreve.setVisibility(View.GONE);
            }


        }
        return v;
    }
}