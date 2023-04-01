package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnLibriFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Libro;

import java.util.ArrayList;

public class LibroViewHolder extends MyViewHolderNoTop {
    public TextView vTitolo, vNumeroCapitolo;
    public ArrayList<Libro> libri;
    public int posizione;


    public static LibroViewHolder newInstance(ViewGroup viewGroup, OnLibriFragmentInteractionListener mListener,
                                              Context c, boolean ultimo) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_libro_card, viewGroup, false);
        return new LibroViewHolder(itemView, mListener, c, ultimo);
    }

    private LibroViewHolder(View v, final OnLibriFragmentInteractionListener mListener,
                            Context c, boolean ultimo) {
        super(v, c, false, ultimo);
        vTitolo = (TextView) v.findViewById(R.id.titolo_libro);
        vNumeroCapitolo = (TextView) v.findViewById(R.id.numero_capitoli);
        if (Preferenze.ottieniMostraNumCapitoli(c)) {
            vNumeroCapitolo.setVisibility(TextView.VISIBLE);
        } else {
            vNumeroCapitolo.setVisibility(TextView.GONE);
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLibriFragmentInteraction(libri, posizione);
            }
        });
    }
}