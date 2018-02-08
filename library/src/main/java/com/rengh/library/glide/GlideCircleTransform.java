
package com.rengh.library.glide;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.rengh.library.util.common.BitmapUtils;

/**
 * Created by rengh on 17-6-27.
 */
public class GlideCircleTransform extends BitmapTransformation {
    private int mWidth = 0;
    private int mHeight = 0;
    private int mPixels = 0;

    public GlideCircleTransform(Context context, int pixels) {
        super(context);
        mPixels = pixels;
    }

    public GlideCircleTransform(Context context, int width, int height, int pixels) {
        super(context);
        mWidth = width;
        mHeight = height;
        mPixels = pixels;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null)
            return null;

        if (0 == mHeight && 0 == mWidth) {
            return BitmapUtils.toRoundCornerImage(source, mPixels);
        } else {
            return BitmapUtils.toRoundCornerImage(source, mWidth, mHeight, mPixels);
        }
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
