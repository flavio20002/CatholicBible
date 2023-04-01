package com.barisi.flavio.bibbiacattolica.gui.fastScroll;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.AbsSectionIndicator;


/**
 * Defines a basic widget that will allow for fast scrolling a RecyclerView using the basic paradigm of
 * a handle and a bar.
 * <p/>
 */
public abstract class AbsFastScroller extends FrameLayout {

    @Nullable
    private VerticalScrollProgressCalculator mScrollProgressCalculator;

    VerticalScreenPositionCalculator mScreenPositionCalculator;

    /**
     * The long bar along which a handle travels
     */
    private final View mBar;
    
    private AppBarLayout mAppBar;
    AbsSectionIndicator<String> mSectionIndicator;
    private int lastPosition = -1;

    /**
     * If I had my druthers, AbsRecyclerViewFastScroller would implement this as an interface, but Android has made
     * {@link OnScrollListener} an abstract class instead of an interface. Hmmm
     */
    public AbsFastScroller(Context context) {
        this(context, null, 0);
    }

    public AbsFastScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsFastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(getLayoutResourceId(), this, true);
        mBar = findViewById(R.id.scroll_bar);
        setOnTouchListener(new FastScrollerTouchListener(this, needTimer()));
    }

    protected abstract boolean needTimer();

    public void setAppBar(AppBarLayout appBar) {
        mAppBar = appBar;
    }

    public void setSectionIndicator(AbsSectionIndicator<String> sectionIndicator) {
        mSectionIndicator = sectionIndicator;
    }

    @Nullable
    AbsSectionIndicator getSectionIndicator() {
        return mSectionIndicator;
    }

    void scrollTo(float scrollProgress) {
        if (!ready()) {
            return;
        }
        int position = getPositionFromScrollProgress(scrollProgress);
        if (position == 0) {
            mAppBar.setExpanded(true, false);
        } else {
            mAppBar.setExpanded(false, false);
        }
        updateSectionIndicator(position, scrollProgress);
        if (lastPosition != position) {
            scrollToPosition(position, scrollProgress);
            lastPosition = position;
        }
    }

    private void updateSectionIndicator(int position, float scrollProgress) {
        if (mSectionIndicator != null) {
            String sel = getSectionForPosition(position);
            if (sel != null) {
                float y = mScreenPositionCalculator.getYPositionFromScrollProgress(scrollProgress) - mSectionIndicator.getHeight();
                if (y < 0) {
                    y = 0;
                }
                mSectionIndicator.setProgress(y);
                mSectionIndicator.setSection(sel);
            }
        }
    }


    private int getPositionFromScrollProgress(float scrollProgress) {
        return (int) ((getItemScrollingCount() - 1) * scrollProgress);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (getScrollProgressCalculator() == null || changed) {
            onCreateScrollProgressCalculator();
        }
    }

    /**
     * Sub classes have to override this method and create the ScrollProgressCalculator instance in this method.
     */

    private void onCreateScrollProgressCalculator() {
        VerticalScrollBoundsProvider boundsProvider =
                new VerticalScrollBoundsProvider(mBar.getY(), mBar.getY() + mBar.getHeight());
        mScrollProgressCalculator = new VerticalScrollProgressCalculator(boundsProvider);
        mScreenPositionCalculator = new VerticalScreenPositionCalculator(boundsProvider);

    }

    /**
     * Takes a touch event and determines how much scroll progress this translates into
     *
     * @param event touch event received by the layout
     * @return scroll progress, or fraction by which list is scrolled [0 to 1]
     */
    float getScrollProgress(MotionEvent event) {
        VerticalScrollProgressCalculator scrollProgressCalculator = getScrollProgressCalculator();
        if (scrollProgressCalculator != null) {
            return getScrollProgressCalculator().calculateScrollProgress(event);
        }
        return 0;
    }

    /**
     * Define a layout resource for your implementation of AbsFastScroller
     * Currently must contain a handle view (R.id.scroll_handle) and a bar (R.id.scroll_bar)
     *
     * @return a resource id corresponding to the chosen layout.
     */
    private int getLayoutResourceId() {
        return R.layout.my_vertical_fast_scroller_layout;
    }

    @Nullable
    private VerticalScrollProgressCalculator getScrollProgressCalculator() {
        return mScrollProgressCalculator;
    }

    protected abstract boolean ready();

    protected abstract int getItemScrollingCount();

    protected abstract String getSectionForPosition(int position);

    protected abstract void scrollToPosition(int i, float scrollProgress);

}