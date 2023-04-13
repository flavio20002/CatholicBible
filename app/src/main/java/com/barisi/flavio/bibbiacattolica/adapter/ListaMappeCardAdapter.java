package com.barisi.flavio.bibbiacattolica.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.adapter.holder.MappaViewHolder;
import com.barisi.flavio.bibbiacattolica.interfaces.OnMappaInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Mappa;

import java.util.List;

public class ListaMappeCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Mappa> mappe;
    private OnMappaInteractionListener mListener;


    public ListaMappeCardAdapter(List<Mappa> mappe, OnMappaInteractionListener mListener) {
        this.mappe = mappe;
        this.mListener = mListener;
    }

    public void addItems(List<Mappa> mappe) {
        this.mappe.addAll(mappe);
    }

    public void clearItems() {
        mappe.clear();
    }

    @Override
    public int getItemCount() {
        return mappe.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Mappa mappa = mappe.get(i);
        MappaViewHolder mappaViewHolder = (MappaViewHolder) viewHolder;
        mappaViewHolder.vNomeMappa.setText(mappa.getNomeMappa());
        mappaViewHolder.vAnno.setText(mappa.getAnno());
        mappaViewHolder.currentItem = mappa;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return MappaViewHolder.newInstance(viewGroup, mListener);
    }


}