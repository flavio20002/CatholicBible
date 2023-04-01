package com.barisi.flavio.bibbiacattolica.gui.fastScroll;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.barisi.flavio.bibbiacattolica.Utility;

import java.util.List;

public class VerticalWebViewFastScroller extends AbsFastScroller {

    private WebView mWebView;
    private List<String> mNumeriVersetti;

    public VerticalWebViewFastScroller(Context context) {
        super(context);
    }

    public VerticalWebViewFastScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalWebViewFastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected boolean needTimer() {
        return true;
    }

    @Override
    protected boolean ready() {
        return mWebView != null && mNumeriVersetti != null;
    }

    public void setWebView(WebView webView) {
        mWebView = webView;
    }

    public void setVersetti(List<String> numeriVersetti) {
        mNumeriVersetti = numeriVersetti;
    }

    protected int getItemScrollingCount() {
        if (mNumeriVersetti.size() > 0) {
            return mNumeriVersetti.size();
        } else {
            return 100;
        }
    }

    protected String getSectionForPosition(int position) {
        if (mNumeriVersetti.size() > 0) {
            return mNumeriVersetti.get(position);
        } else {
            return "";
        }
    }

    protected void scrollToPosition(int position, float scrollProgress) {
        if (mNumeriVersetti.size() > 0) {
            if (position < 1) {
                Utility.eseguiJavascript(mWebView, "myScrollToPercentage(0);");
            } else if (position >= mNumeriVersetti.size() - 1) {
                Utility.eseguiJavascript(mWebView, "myScrollToPercentage(1);");
            } else {
                Utility.eseguiJavascript(mWebView, "myScrollToTagHtmlFast('sup','" + mNumeriVersetti.get(position) + "'," + scrollProgress + ");");
            }
        } else {
            Utility.eseguiJavascript(mWebView, "myScrollToPercentage(" + scrollProgress + ");");
        }
    }

}