package com.barisi.flavio.bibbiacattolica.adapter.holder;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.barisi.flavio.bibbiacattolica.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public MyViewHolder(View v, Context c, boolean ultimo) {
        super(v);
        CardView card = (CardView) v;
        int margin = c.getResources().getDimensionPixelSize(R.dimen.my_margin);
        CardView.LayoutParams layoutParams = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        if (ultimo) {
            layoutParams.setMargins(margin, margin, margin, margin);
        } else {
            layoutParams.setMargins(margin, margin, margin, 0);
        }
        card.setLayoutParams(layoutParams);

    }

}