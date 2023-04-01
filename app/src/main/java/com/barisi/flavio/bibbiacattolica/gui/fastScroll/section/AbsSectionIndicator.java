package com.barisi.flavio.bibbiacattolica.gui.fastScroll.section;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * Abstract base implementation of a section indicator used to indicate the section of a list upon which the user is
 * currently fast scrolling.
 */
public abstract class AbsSectionIndicator<T> extends FrameLayout {

    private DefaultSectionIndicatorAlphaAnimator mDefaultSectionIndicatorAlphaAnimator;

    public AbsSectionIndicator(Context context) {
        this(context, null);
    }

    public AbsSectionIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsSectionIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(getDefaultLayoutId(), this, true);
        mDefaultSectionIndicatorAlphaAnimator = new DefaultSectionIndicatorAlphaAnimator(this);
    }

    /**
     * @return the default layout for a given implementation of AbsSectionIndicator
     */
    protected abstract int getDefaultLayoutId();

    public void setProgress(float progress) {
        setY(progress);
    }

    public void animateAlpha(float targetAlpha) {
        mDefaultSectionIndicatorAlphaAnimator.animateTo(targetAlpha);
    }

    public abstract void setSection(T object);

}
