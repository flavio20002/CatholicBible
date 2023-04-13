package com.barisi.flavio.bibbiacattolica.adapter.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;

public class IntroduzioneGridViewHolder extends RecyclerView.ViewHolder {
    public TextView vSiglaLibro;
    public Capitolo currentItem;

    public static IntroduzioneGridViewHolder newInstance(ViewGroup viewGroup, OnArticleFragmentInteractionListener mListener) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_capitolo_introduzione_grid, viewGroup, false);
        return new IntroduzioneGridViewHolder(itemView, mListener);
    }

    private IntroduzioneGridViewHolder(View v, final OnArticleFragmentInteractionListener mListener) {
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