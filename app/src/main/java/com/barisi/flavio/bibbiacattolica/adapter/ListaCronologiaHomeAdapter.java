package com.barisi.flavio.bibbiacattolica.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;

import java.util.List;

class ListaCronologiaHomeAdapter extends BaseAdapter {

    private Context mContext;
    private List<Capitolo> capitoli;

    ListaCronologiaHomeAdapter(Context c, List<Capitolo> capitoli) {
        this.capitoli = capitoli;
        this.mContext = c;
    }

    @Override
    public int getCount() {
        return capitoli.size();
    }

    @Override
    public Capitolo getItem(int position) {
        return capitoli.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final Capitolo i = capitoli.get(position);
        if (i != null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.elemento_lista_cronologia_home, null);
            final TextView title =
                    (TextView) v.findViewById(R.id.titolo_libro);
            if (title != null) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                title.setText(String.format(mContext.getString(R.string.capitoloRicerca), i.getNomeLibro(), i.getNumero()));
            }
            final TextView numeroCapitoli =
                    (TextView) v.findViewById(R.id.numero_capitoli);
            if (numeroCapitoli != null) {
                numeroCapitoli.setText(i.getTitolo());
                if (Regex.stringaVuota(i.getTitolo())) {
                    numeroCapitoli.setVisibility(View.GONE);
                } else {
                    numeroCapitoli.setVisibility(View.VISIBLE);
                    numeroCapitoli.setText(i.getTitolo());
                    numeroCapitoli.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                }
            }


        }
        return v;
    }
}