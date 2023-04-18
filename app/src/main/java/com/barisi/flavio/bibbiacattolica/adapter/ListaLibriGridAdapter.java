package com.barisi.flavio.bibbiacattolica.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.adapter.holder.LibroGridViewHolder;
import com.barisi.flavio.bibbiacattolica.interfaces.OnLibriFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Categoria;
import com.barisi.flavio.bibbiacattolica.model.Libro;

import java.util.ArrayList;
import java.util.List;

public class ListaLibriGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Libro> libri;
    private OnLibriFragmentInteractionListener mListener;

    public ListaLibriGridAdapter(OnLibriFragmentInteractionListener mListener) {
        this.mListener = mListener;
        libri = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return libri.size();
    }

    public void addItems(List<Categoria> categorie) {
        libri.clear();
        for (int i = 0; i < categorie.size(); i++) {
            Categoria cat = categorie.get(i);
            if (cat.getListaLibri() != null) {
                for (int j = 0; j < cat.getListaLibri().size(); j++) {
                    libri.add(cat.getListaLibri().get(j));
                    Libro lib = cat.getListaLibri().get(j);
                    lib.setCategoria(cat.getDesGategoria());
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Libro lib = libri.get(i);
        LibroGridViewHolder lvh = (LibroGridViewHolder) viewHolder;
        lvh.vSiglaLibro.setText(lib.getAbbreviazione());
        lvh.libri = libri;
        lvh.posizione = libri.indexOf(lib);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return LibroGridViewHolder.newInstance(viewGroup, mListener);
    }

}