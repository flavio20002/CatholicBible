package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;

public class CapitoloViewHolder extends MyViewHolder {
    public TextView vNumeroCapitolo;
    public TextView vTitoloCapitolo;
    public TextView vTestoCapitolo;
    public Capitolo currentItem;


    public static CapitoloViewHolder newInstance(ViewGroup viewGroup, OnArticleFragmentInteractionListener mListener,
                                                 Context c, boolean ultimo, int maxLines) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_lista_articolo_card, viewGroup, false);
        return new CapitoloViewHolder(itemView, mListener, c, ultimo, maxLines);
    }

    public CapitoloViewHolder(View v, final OnArticleFragmentInteractionListener mListener,
                              Context c, boolean ultimo, int maxLines) {
        super(v, c, ultimo);
        vNumeroCapitolo = (TextView) v.findViewById(R.id.numero_capitolo);
        vTitoloCapitolo = (TextView) v.findViewById(R.id.titolo_capitolo);
        vTestoCapitolo = (TextView) v.findViewById(R.id.testo_capitolo);
        vTestoCapitolo.setMaxLines(maxLines);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onArticleFragmentInteraction(currentItem.getId(), currentItem.getFiltro());
            }
        });
    }
}