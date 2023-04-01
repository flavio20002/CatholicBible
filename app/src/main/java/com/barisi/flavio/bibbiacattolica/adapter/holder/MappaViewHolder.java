package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnMappaInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Mappa;

public class MappaViewHolder extends RecyclerView.ViewHolder {
    public TextView vNomeMappa;
    public TextView vAnno;
    public Mappa currentItem;


    public static MappaViewHolder newInstance(ViewGroup viewGroup, OnMappaInteractionListener mListener) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_lista_mappe_card, viewGroup, false);
        return new MappaViewHolder(itemView, mListener);
    }

    private MappaViewHolder(View v, final OnMappaInteractionListener mListener) {
        super(v);
        vNomeMappa = (TextView) v.findViewById(R.id.nome_mappa);
        vAnno = (TextView) v.findViewById(R.id.anno);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onmappaInteraction(currentItem);
            }
        });
    }
}