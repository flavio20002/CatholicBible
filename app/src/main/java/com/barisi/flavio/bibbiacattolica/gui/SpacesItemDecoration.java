package com.barisi.flavio.bibbiacattolica.gui;

import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.top = space;

        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
            int numeroElementi = parent.getAdapter().getItemCount();
            int posizione = parent.getChildLayoutPosition(view);
            int numeroRighe = (int) Math.ceil(numeroElementi / spanCount);
            int riga = (int) Math.ceil(posizione / spanCount);
            if (riga == numeroRighe) {
                outRect.bottom = space;
            }
        }
    }
}