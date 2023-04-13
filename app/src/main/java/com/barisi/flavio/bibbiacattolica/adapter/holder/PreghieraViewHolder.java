package com.barisi.flavio.bibbiacattolica.adapter.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnPreghieraInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Preghiera;

public class PreghieraViewHolder extends RecyclerView.ViewHolder {
    public TextView vNomePreghiera;
    public Preghiera currentItem;


    public static PreghieraViewHolder newInstance(ViewGroup viewGroup, OnPreghieraInteractionListener mListener) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_lista_preghiere_card, viewGroup, false);
        return new PreghieraViewHolder(itemView, mListener);
    }

    private PreghieraViewHolder(View v, final OnPreghieraInteractionListener mListener) {
        super(v);
        vNomePreghiera = (TextView) v.findViewById(R.id.nome_preghiera);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPreghieraInteration(currentItem);
            }
        });
    }
}