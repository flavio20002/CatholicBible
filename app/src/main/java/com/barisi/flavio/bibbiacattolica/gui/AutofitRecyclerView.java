package com.barisi.flavio.bibbiacattolica.gui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.barisi.flavio.bibbiacattolica.R;

public class AutofitRecyclerView extends RecyclerView {
    private int columnWidth = -1;

    public AutofitRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        columnWidth = context.getResources().getDimensionPixelSize(R.dimen.column_width);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0 && getLayoutManager() instanceof GridLayoutManager) {
            int newWidth = (int) (columnWidth * getContext().getResources().getConfiguration().fontScale);
            int spanCount = Math.max(1, getMeasuredWidth() / newWidth);
            ((GridLayoutManager) getLayoutManager()).setSpanCount(spanCount);
        }
    }
}