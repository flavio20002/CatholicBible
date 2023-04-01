package com.barisi.flavio.bibbiacattolica.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.adapter.holder.CapitoloGridViewHolder;
import com.barisi.flavio.bibbiacattolica.adapter.holder.IntroduzioneGridViewHolder;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ListaCapitoliGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Capitolo> capitolos;
    private OnArticleFragmentInteractionListener mListener;

    public ListaCapitoliGridAdapter(OnArticleFragmentInteractionListener mListener) {
        this.capitolos = new ArrayList<>();
        this.mListener = mListener;
    }

    public void addItems(List<Capitolo> capitolos) {
        this.capitolos.clear();
        this.capitolos.addAll(capitolos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return capitolos.size();
    }

    @Override
    public int getItemViewType(int position) {
        Capitolo c = capitolos.get(position);
        if (c.getNumero() == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Capitolo capitolo = capitolos.get(i);
        if (capitolo.getNumero() == 0) {
            IntroduzioneGridViewHolder ivh = (IntroduzioneGridViewHolder) viewHolder;
            ivh.vSiglaLibro.setText(R.string.intro);
            ivh.currentItem = capitolo;
        } else {
            CapitoloGridViewHolder contactViewHolder = (CapitoloGridViewHolder) viewHolder;
            contactViewHolder.vSiglaLibro.setText(String.valueOf(capitolo.getNumero()));
            contactViewHolder.currentItem = capitolo;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            return IntroduzioneGridViewHolder.newInstance(viewGroup, mListener);
        }
        return CapitoloGridViewHolder.newInstance(viewGroup, mListener);
    }

}