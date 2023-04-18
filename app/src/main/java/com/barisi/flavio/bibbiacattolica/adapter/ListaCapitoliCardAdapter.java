package com.barisi.flavio.bibbiacattolica.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.adapter.holder.CapitoloViewHolder;
import com.barisi.flavio.bibbiacattolica.adapter.holder.IntroduzioneViewHolder;
import com.barisi.flavio.bibbiacattolica.adapter.holder.MyViewHolder;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.MySectionIndexer;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.model.Capitolo;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ListaCapitoliCardAdapter extends RecyclerView.Adapter<MyViewHolder> implements MySectionIndexer {

    private List<Capitolo> capitolos;
    private OnArticleFragmentInteractionListener mListener;
    private Context mContext;


    public ListaCapitoliCardAdapter(OnArticleFragmentInteractionListener mListener,
                                    Context mContext) {
        this.capitolos = new ArrayList<>();
        this.mListener = mListener;
        this.mContext = mContext;
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
            if (position != capitolos.size() - 1) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        Capitolo capitolo = capitolos.get(i);
        if (capitolo.getNumero() == 0) {
            IntroduzioneViewHolder ivh = (IntroduzioneViewHolder) viewHolder;
            ivh.vTitolo.setText(R.string.introduzione);
            ivh.vTitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            ivh.vTesto.setText(R.string.introduzione_des);
            ivh.vTesto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            ivh.currentItem = capitolo;
        } else {
            CapitoloViewHolder contactViewHolder = (CapitoloViewHolder) viewHolder;
            if (capitolo.getId().startsWith("Sal_")) {
                contactViewHolder.vNumeroCapitolo.setText(String.format(mContext.getString(R.string.salmo), capitolo.getNumero()));
            } else {
                contactViewHolder.vNumeroCapitolo.setText(String.format(mContext.getString(R.string.capitolo), capitolo.getNumero()));
            }
            contactViewHolder.vNumeroCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            if (capitolo.getTitolo() == null || capitolo.getTitolo().equals("")) {
                contactViewHolder.vTitoloCapitolo.setVisibility(View.GONE);
            } else {
                contactViewHolder.vTitoloCapitolo.setVisibility(View.VISIBLE);
                contactViewHolder.vTitoloCapitolo.setText(capitolo.getTitolo());
                contactViewHolder.vTitoloCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            }
            if (capitolo.getTestoBreve() == null || capitolo.getTestoBreve().equals("")) {
                contactViewHolder.vTestoCapitolo.setVisibility(View.GONE);
            } else {
                contactViewHolder.vTestoCapitolo.setVisibility(View.VISIBLE);
                contactViewHolder.vTestoCapitolo.setText(Html.fromHtml(capitolo.getTestoBreve()).toString().replace("\n", ""));
                contactViewHolder.vTestoCapitolo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            }
            contactViewHolder.currentItem = capitolo;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == 0) {
            return IntroduzioneViewHolder.newInstance(viewGroup, mListener, mContext);
        } else if (i == 1) {
            return CapitoloViewHolder.newInstance(viewGroup, mListener, mContext, false, 3);
        } else {
            return CapitoloViewHolder.newInstance(viewGroup, mListener, mContext, true, 3);
        }
    }

    @Override
    public String getSectionForPosition(int position) {
        int num = capitolos.get(position).getNumero();
        if (num == 0) {
            return mContext.getString(R.string.intro);
        } else {
            return String.valueOf(num);
        }
    }
}