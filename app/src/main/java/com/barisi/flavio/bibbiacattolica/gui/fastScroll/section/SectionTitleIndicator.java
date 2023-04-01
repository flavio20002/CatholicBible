package com.barisi.flavio.bibbiacattolica.gui.fastScroll.section;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.barisi.flavio.bibbiacattolica.Preferenze;
import com.barisi.flavio.bibbiacattolica.R;
import com.barisi.flavio.bibbiacattolica.Utility;


/**
 * Popup view that gets shown when fast scrolling
 */
public class SectionTitleIndicator extends AbsSectionIndicator<String> {

    private static final int DEFAULT_TITLE_INDICATOR_LAYOUT = R.layout.section_indicator_with_title;

    private final View mIndicatorBackground;
    private final TextView mTitleText;

    public SectionTitleIndicator(Context context) {
        this(context, null);
    }

    public SectionTitleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionTitleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mIndicatorBackground = findViewById(R.id.section_title_popup);
        mIndicatorBackground.getBackground().setColorFilter(Preferenze.colorePrincipaleScuro(context), PorterDuff.Mode.SRC_ATOP);
        mIndicatorBackground.getBackground().setAlpha(220);
        mTitleText = (TextView) findViewById(R.id.section_indicator_text);
    }

    /**
     * @return the default layout for a section indicator with a title. This closely resembles the section indicator
     * featured in Lollipop's Contact's application
     */
    @Override
    protected int getDefaultLayoutId() {
        return DEFAULT_TITLE_INDICATOR_LAYOUT;
    }

    @Override
    public void setSection(String s) {
        mTitleText.setText(s);
    }

}
