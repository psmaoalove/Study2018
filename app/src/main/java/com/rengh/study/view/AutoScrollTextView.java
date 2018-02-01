package com.rengh.study.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by rengh on 18-1-31.
 */
public class AutoScrollTextView extends AppCompatTextView {
    public AutoScrollTextView(Context context) {
        super(context);
        setDefProperties();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefProperties();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDefProperties();
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    private void setDefProperties() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setSingleLine(true);
        setMarqueeRepeatLimit(-1);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setHorizontallyScrolling(true);
    }

}
