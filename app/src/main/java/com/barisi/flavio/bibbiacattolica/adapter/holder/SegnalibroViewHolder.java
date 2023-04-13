package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Regex;
import com.barisi.flavio.bibbiacattolica.interfaces.OnArticleFragmentInteractionListener;
import com.barisi.flavio.bibbiacattolica.interfaces.SegnalibriListener;
import com.barisi.flavio.bibbiacattolica.model.Segnalibro;

public class SegnalibroViewHolder extends MyViewHolder {
    public TextView vNumeroCapitolo;
    public TextView vTestoCapitolo;
    public TextView vNotaSegnalibro;
    public Segnalibro currentItem;


    public static SegnalibroViewHolder newInstance(ViewGroup viewGroup, OnArticleFragmentInteractionListener mListener, SegnalibriListener sListener,
                                                   Context c, boolean ultimo) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.elemento_lista_segnalibri_card, viewGroup, false);
        return new SegnalibroViewHolder(itemView, mListener, sListener, c, ultimo);
    }

    private SegnalibroViewHolder(View v, final OnArticleFragmentInteractionListener mListener, final SegnalibriListener sListener,
                                 final Context c, boolean ultimo) {
        super(v, c, ultimo);
        vNumeroCapitolo = (TextView) v.findViewById(R.id.numero_capitolo);
        vTestoCapitolo = (TextView) v.findViewById(R.id.testo_capitolo);
        vNotaSegnalibro = (TextView) v.findViewById(R.id.nota_segnalibro);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Regex.stringaNonVuota(currentItem.getVersetto())) {
                    mListener.onArticleFragmentInteraction(currentItem.getIdCapitolo(), "#" + currentItem.getVersetto());
                } else {
                    mListener.onArticleFragmentInteraction(currentItem.getIdCapitolo(), null);
                }
            }
        });
        AppCompatImageView overflow = (AppCompatImageView) v.findViewById(R.id.overflow);
        overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.overflow_segnalibri);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.cancella_segnalibri: {
                                sListener.cancellaSegnalibro(currentItem, true);
                                break;
                            }
                            case R.id.modifica_nota_segnalibri: {
                                sListener.modificaNotaSegnalibro(currentItem);
                                break;
                            }
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }
}