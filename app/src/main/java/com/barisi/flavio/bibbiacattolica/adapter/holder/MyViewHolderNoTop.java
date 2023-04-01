package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.barisi.flavio.bibbiacattolica.R;

public class MyViewHolderNoTop extends RecyclerView.ViewHolder {
    public CardView card;

    public MyViewHolderNoTop(View v, Context c, boolean titolo, boolean ultimo) {
        super(v);
        card = (CardView) v;
        if (ultimo) {
            int margin = c.getResources().getDimensionPixelSize(R.dimen.my_margin);
            int marginDummy = c.getResources().getDimensionPixelSize(R.dimen.my_margin_dummy);
            CardView.LayoutParams layoutParams = new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
            if (titolo) {
                layoutParams.setMargins(margin, margin, margin, 0);
            } else {
                if (ultimo) {
                    layoutParams.setMargins(margin, 0, margin, margin);
                } else {
                    layoutParams.setMargins(margin, 0, margin, marginDummy);
                }
            }
            card.setLayoutParams(layoutParams);
        }
    }

    ;

}