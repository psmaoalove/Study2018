
package com.rengh.library.util.common;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by rengh on 17-5-29.
 */
public class UIUtils {
    public static Resources getResources(Context context) {
        return context.getResources();
    }

    public static int dip2px(Context context, float dip) {
        float density = getResources(context).getDisplayMetrics().density;
        return (int) (dip * density + 0.5);
    }

    public static int px2dip(Context context, int px) {
        float density = getResources(context).getDisplayMetrics().density;
        return (int) (px / density + 0.5);
    }

    public static float sp2px(Context context, float sp) {
        final float scale = getResources(context).getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static void scaleView(View view, float value) {
        scaleView(view, value, 300);
    }

    public static void scaleView(View view, float value, int duration) {
        if (view != null) {
            ObjectAnimator animx = ObjectAnimator.ofFloat(view, "scaleX", value);
            ObjectAnimator animy = ObjectAnimator.ofFloat(view, "scaleY", value);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(duration);
            set.setInterpolator(new DecelerateInterpolator());
            set.play(animx).with(animy);
            set.start();
        }
    }
}
