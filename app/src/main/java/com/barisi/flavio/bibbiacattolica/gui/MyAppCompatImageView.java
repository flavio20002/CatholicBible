package com.barisi.flavio.bibbiacattolica.gui;


import android.content.Context;
import android.graphics.Rect;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by barisi on 18/10/2016.
 */

public class MyAppCompatImageView extends AppCompatImageView {
    public MyAppCompatImageView(Context context) {
        super(context);
    }

    public MyAppCompatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAppCompatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        return false;
    }
}
