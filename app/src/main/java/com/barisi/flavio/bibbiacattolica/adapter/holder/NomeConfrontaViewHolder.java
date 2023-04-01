package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;

public class NomeConfrontaViewHolder extends RecyclerView.ViewHolder {
    public TextView vNomeConfronto1, vNomeConfronto2, vNomeConfronto3;
    public Space spazioInPiu;

    public static NomeConfrontaViewHolder newInstance(ViewGroup viewGroup) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_nome_confronto, viewGroup, false);
        return new NomeConfrontaViewHolder(itemView);
    }

    NomeConfrontaViewHolder(View v) {
        super(v);
        vNomeConfronto1 = (TextView) v.findViewById(R.id.nome_confronto1);
        vNomeConfronto2 = (TextView) v.findViewById(R.id.nome_confronto2);
        vNomeConfronto3 = (TextView) v.findViewById(R.id.nome_confronto3);
        spazioInPiu = (Space) v.findViewById(R.id.spazio_in_piu);
    }
}