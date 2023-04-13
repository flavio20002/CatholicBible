package com.barisi.flavio.bibbiacattolica.gui.fastScroll;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.MySectionIndexer;

public class VerticalRecyclerViewFastScroller extends AbsFastScroller {

    private RecyclerView mRecyclerView;

    public VerticalRecyclerViewFastScroller(Context context) {
        super(context);
    }

    public VerticalRecyclerViewFastScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalRecyclerViewFastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean needTimer() {
        return false;
    }

    @Override
    protected boolean ready() {
        if (mRecyclerView != null && mRecyclerView.getAdapter() != null) {
            return true;
        }
        return false;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    protected int getItemScrollingCount() {
        return mRecyclerView.getAdapter().getItemCount();
    }

    protected String getSectionForPosition(int position) {
        if (mRecyclerView.getAdapter() instanceof MySectionIndexer) {
            MySectionIndexer indexer = ((MySectionIndexer) mRecyclerView.getAdapter());
            return indexer.getSectionForPosition(position);
        }
        return null;
    }

    protected void scrollToPosition(int position, float scrollProgress) {
        if (position == 0) {
            mRecyclerView.scrollToPosition(0);
        } else {
            if (position >= mRecyclerView.getAdapter().getItemCount() - 1) {
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            } else {
                LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int margin = getContext().getResources().getDimensionPixelSize(R.dimen.my_margin);
                int pos = (int) mScreenPositionCalculator.getYPositionFromScrollProgress(scrollProgress) - margin - mSectionIndicator.getHeight();
                llm.scrollToPositionWithOffset(position, pos);
            }
        }
    }

}