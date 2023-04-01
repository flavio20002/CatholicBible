package com.barisi.flavio.bibbiacattolica.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.adapter.holder.MyViewHolder;
import com.barisi.flavio.bibbiacattolica.adapter.holder.SegnalibroViewHolder;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.SegnalibriListener;
import com.barisi.flavio.bibbiacattolica.model.Segnalibro;

import java.util.List;

public class ListaSegnalibriCardAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<Segnalibro> segnalibri;
    private OnArticleFragmentInteractionListener mListener;
    private SegnalibriListener sListener;
    private Context mContext;


    public ListaSegnalibriCardAdapter(List<Segnalibro> segnalibri, OnArticleFragmentInteractionListener mListener,
                                      SegnalibriListener sListener, Context mContext) {
        this.segnalibri = segnalibri;
        this.mListener = mListener;
        this.sListener = sListener;
        this.mContext = mContext;
    }

    public void clearItems() {
        this.segnalibri.clear();
    }

    public void addItems(List<Segnalibro> capitolos) {
        this.segnalibri.addAll(capitolos);
    }

    public List<Segnalibro> getSegnalibri() {
        return segnalibri;
    }

    @Override
    public int getItemCount() {
        return segnalibri.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        Segnalibro capitolo = segnalibri.get(i);
        SegnalibroViewHolder contactViewHolder = (SegnalibroViewHolder) viewHolder;

        contactViewHolder.vNumeroCapitolo.setText(capitolo.getRiferimento());

        if (Regex.stringaVuota(capitolo.getTestoBreve())) {
            contactViewHolder.vTestoCapitolo.setVisibility(View.GONE);
        } else {
            contactViewHolder.vTestoCapitolo.setVisibility(View.VISIBLE);
            contactViewHolder.vTestoCapitolo.setText(capitolo.getTestoBreve());
        }

        if (Regex.stringaVuota(capitolo.getNota())) {
            contactViewHolder.vNotaSegnalibro.setVisibility(View.GONE);
        } else {
            contactViewHolder.vNotaSegnalibro.setVisibility(View.VISIBLE);
            contactViewHolder.vNotaSegnalibro.setText(capitolo.getNota());
        }
        contactViewHolder.currentItem = capitolo;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return SegnalibroViewHolder.newInstance(viewGroup, mListener, sListener, mContext, false);
    }
}