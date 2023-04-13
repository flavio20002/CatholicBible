package com.barisi.flavio.bibbiacattolica.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.adapter.holder.PreghieraViewHolder;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.MySectionIndexer;
import com.barisi.flavio.bibbiacattolica.interfaces.OnPreghieraInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Preghiera;

import java.util.List;

public class ListaPreghiereCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements MySectionIndexer {

    private List<Preghiera> preghiere;
    private OnPreghieraInteractionListener mListener;


    public ListaPreghiereCardAdapter(List<Preghiera> preghiere, OnPreghieraInteractionListener mListener) {
        this.preghiere = preghiere;
        this.mListener = mListener;
    }

    public void addItems(List<Preghiera> preghiere) {
        this.preghiere.addAll(preghiere);
    }

    public void clearItems() {
        preghiere.clear();
    }

    @Override
    public int getItemCount() {
        return preghiere.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Preghiera preghiera = preghiere.get(i);
        PreghieraViewHolder preghieraViewHolder = (PreghieraViewHolder) viewHolder;
        preghieraViewHolder.vNomePreghiera.setText(preghiera.getNomepreghiera());
        preghieraViewHolder.currentItem = preghiera;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return PreghieraViewHolder.newInstance(viewGroup, mListener);
    }


    @Override
    public String getSectionForPosition(int position) {
        Preghiera obj = preghiere.get(position);
        System.out.println(position);
        return String.valueOf(obj.getNomepreghiera().charAt(0));
    }
}