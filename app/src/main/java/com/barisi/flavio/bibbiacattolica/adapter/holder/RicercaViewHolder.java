package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;

public class RicercaViewHolder extends MyViewHolder {
    public TextView vNumeroCapitolo;
    public TextView vTitoloCapitolo;
    public TextView vTestoCapitolo;
    public Capitolo currentItem;


    public static RicercaViewHolder newInstance(ViewGroup viewGroup, OnArticleFragmentInteractionListener mListener,
                                                Context c, boolean ultimo) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_lista_ricerca_card, viewGroup, false);
        return new RicercaViewHolder(itemView, mListener, c, ultimo);
    }

    public RicercaViewHolder(View v, final OnArticleFragmentInteractionListener mListener,
                             Context c, boolean ultimo) {
        super(v, c, ultimo);
        vNumeroCapitolo = (TextView) v.findViewById(R.id.numero_capitolo);
        vTitoloCapitolo = (TextView) v.findViewById(R.id.titolo_capitolo);
        vTestoCapitolo = (TextView) v.findViewById(R.id.testo_capitolo);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onArticleFragmentInteraction(currentItem.getId(), currentItem.getFiltro());
            }
        });
    }
}