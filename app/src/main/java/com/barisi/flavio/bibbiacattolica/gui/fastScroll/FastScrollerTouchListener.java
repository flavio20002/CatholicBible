package com.barisi.flavio.bibbiacattolica.gui.fastScroll;


import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.barisi.flavio.bibbiacattolica.gui.fastScroll.section.AbsSectionIndicator;

class FastScrollerTouchListener implements OnTouchListener {

    private final AbsFastScroller mFastScroller;
    private boolean mTimer;
    private Handler handler;


    FastScrollerTouchListener(AbsFastScroller fastScroller, boolean timer) {
        mFastScroller = fastScroller;
        mTimer = timer;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        AbsSectionIndicator sectionIndicator = mFastScroller.getSectionIndicator();
        if (mTimer) {
            showOrHideIndicatorTimer(sectionIndicator, event);
        } else {
            showOrHideIndicator(sectionIndicator, event);
        }

        float scrollProgress = mFastScroller.getScrollProgress(event);
        mFastScroller.scrollTo(scrollProgress);
        return true;
    }

    private void showOrHideIndicatorTimer(final @Nullable AbsSectionIndicator sectionIndicator, MotionEvent event) {
        if (sectionIndicator == null) {
            return;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                sectionIndicator.animateAlpha(1f);
                return;
            case MotionEvent.ACTION_UP:
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                sectionIndicator.animateAlpha(0f);
            case MotionEvent.ACTION_MOVE:
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sectionIndicator.animateAlpha(0f);
                    }
                }, 2500);
        }
    }

    private void showOrHideIndicator(@Nullable AbsSectionIndicator sectionIndicator, MotionEvent event) {
        if (sectionIndicator == null) {
            return;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                sectionIndicator.animateAlpha(1f);
                return;
            case MotionEvent.ACTION_UP:
                sectionIndicator.animateAlpha(0f);
        }
    }

}
