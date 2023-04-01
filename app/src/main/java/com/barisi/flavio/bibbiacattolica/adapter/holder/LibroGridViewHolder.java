package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnLibriFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Libro;

import java.util.ArrayList;

public class LibroGridViewHolder extends RecyclerView.ViewHolder {
    public TextView vSiglaLibro;
    public ArrayList<Libro> libri;
    public int posizione;


    public static LibroGridViewHolder newInstance(ViewGroup viewGroup, OnLibriFragmentInteractionListener mListener) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_libro_card_grid, viewGroup, false);
        return new LibroGridViewHolder(itemView, mListener);
    }

    private LibroGridViewHolder(View v, final OnLibriFragmentInteractionListener mListener) {
        super(v);
        vSiglaLibro = (TextView) v.findViewById(R.id.siglaLibro);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLibriFragmentInteraction(libri, posizione);
            }
        });
    }
}