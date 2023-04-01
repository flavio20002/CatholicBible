package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;

public class NumeroRisultatiViewHolder extends RecyclerView.ViewHolder {
    public TextView vNumeroRisultati;

    public static NumeroRisultatiViewHolder newInstance(ViewGroup viewGroup,
                                                        Context c) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_numero_risultati, viewGroup, false);
        return new NumeroRisultatiViewHolder(itemView);
    }

    public NumeroRisultatiViewHolder(View v) {
        super(v);
        vNumeroRisultati = (TextView) v.findViewById(R.id.numero_risultati);
    }
}