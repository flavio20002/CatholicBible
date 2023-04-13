package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;

public class IntroduzioneViewHolder extends MyViewHolder {
    public TextView vTitolo;
    public TextView vTesto;
    public Capitolo currentItem;

    public static IntroduzioneViewHolder newInstance(ViewGroup viewGroup, OnArticleFragmentInteractionListener mListener,
                                                     Context c) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_introduzione_card, viewGroup, false);
        return new IntroduzioneViewHolder(itemView, mListener, c);
    }

    public IntroduzioneViewHolder(View v, final OnArticleFragmentInteractionListener mListener,
                                  Context c) {
        super(v, c, false);
        vTitolo = (TextView) v.findViewById(R.id.titolo_introduzione);
        vTesto = (TextView) v.findViewById(R.id.testo_introduzione);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onArticleFragmentInteraction(currentItem.getId(), currentItem.getFiltro());
            }
        });
    }
}