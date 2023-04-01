package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;

public class CapitoloGridViewHolder extends RecyclerView.ViewHolder {
    public TextView vSiglaLibro;
    public Capitolo currentItem;

    public static CapitoloGridViewHolder newInstance(ViewGroup viewGroup, OnArticleFragmentInteractionListener mListener) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_libro_card_grid, viewGroup, false);
        return new CapitoloGridViewHolder(itemView, mListener);
    }

    private CapitoloGridViewHolder(View v, final OnArticleFragmentInteractionListener mListener) {
        super(v);
        vSiglaLibro = (TextView) v.findViewById(R.id.siglaLibro);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onArticleFragmentInteraction(currentItem.getId(), currentItem.getFiltro());
            }
        });
    }
}