package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.interfaces.OnLibriFragmentInteractionListener;

public class TitoloViewHolder extends MyViewHolderNoTop {
    public TextView vTitoloCategoria;
    public AbsListView vListView;

    public static TitoloViewHolder newInstance(ViewGroup viewGroup,
                                               Context c) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_categoria_card, viewGroup, false);
        return new TitoloViewHolder(itemView, c);
    }

    public TitoloViewHolder(View v, Context c) {
        super(v, c, true, false);
        vTitoloCategoria = (TextView) v.findViewById(R.id.titolo_categoria);
    }
}