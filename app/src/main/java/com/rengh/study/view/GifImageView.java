
package com.rengh.study.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rengh on 17-5-29.
 */
public class GifImageView extends ImageView {
    /**
     * gift动态效果总时长，在未设置时长时默认为1秒
     */
    private static final int DEFAULT_MOVIE_DURATION = 1000;
    /**
     * Gif动画播放速度延迟倍数
     */
    private int mDelay;
    /**
     * Gif图片时长
     */
    private int mLength = 0;
    /**
     * 设置是否循环播放
     */
    private boolean mIsLoop = true;

    /**
     * Movie实例，用来显示gift图片
     */
    private Movie mGifMovie;
    /**
     * 显示gift图片的动态效果的开始时间
     */
    private long mGifMovieStart;
    /**
     * 动态图当前显示第几帧
     */
    private int mCurrentDuration = 0;
    /**
     * 图片离屏幕左边的距离
     */
    private float mLeft2Screen;
    /**
     * 图片离屏幕上边的距离
     */
    private float mTop2Screen;
    /**
     * 图片的缩放比例
     */
    private float mScaleW;
    /**
     * 图片的缩放比例
     */
    private float mScaleH;

    /**
     * 图片在屏幕上显示的宽度
     */
    private int mMeasuredMovieWidth;
    /**
     * 图片在屏幕上显示的高度
     */
    private int mMeasuredMovieHeight;
    /**
     * 是否显示动画,为true表示显示，false表示不显示
     */
    private boolean mVisible = true;
    /**
     * 动画效果是否被暂停
     */
    private volatile boolean mPaused = false;

    public GifImageView(Context context) {
        this(context, null);
    }

    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setViewAttributes(context, attrs, defStyleAttr);
    }

    private void setViewAttributes(Context context, AttributeSet attrs, int defStyle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (null != attrs) {
            int imgId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", -1);
            if (imgId != -1) {
                byte[] bytes = getGiftBytes(imgId);
                if (null != bytes) {
                    mGifMovie = Movie.decodeByteArray(bytes, 0, bytes.length);
                }
            }
        }
    }

    /**
     * 设置gif图资源
     * 
     * @param imgId
     */
    public void setMovieResource(int imgId) throws Exception {
        byte[] bytes = getGiftBytes(imgId);
        if (null != bytes) {
            mGifMovie = Movie.decodeByteArray(bytes, 0, bytes.length);
            requestLayout();
        } else {
            throw new Exception("Read file failed.");
        }
    }

    /**
     * 设置gif图资源
     * 
     * @param imgPath
     */
    public void setImagePath(String imgPath) throws Exception {
        byte[] bytes = getGiftBytes(imgPath);
        if (null != bytes) {
            mGifMovie = Movie.decodeByteArray(bytes, 0, bytes.length);
            requestLayout();
        } else {
            throw new Exception("Read file failed.");
        }
    }

    /**
     * Gif动画播放速度延迟倍数
     * 
     * @param delay
     */
    public void setDelay(int delay) {
        this.mDelay = delay;
    }

    /**
     * 指定Gif图片播放时长，单位：毫秒；指定时长后，Gif图片播放相应的时长，setDealy方法将不起作用。
     * 
     * @param length
     */
    public void setLength(int length) {
        this.mLength = length;
    }

    /**
     * 设置是否循环播放
     * 
     * @param loop
     */
    public void setLoop(boolean loop) {
        this.mIsLoop = loop;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mGifMovie != null) {
            int movieWidth = mGifMovie.width();
            int movieHeight = mGifMovie.height();
            int maximumWidth = MeasureSpec.getSize(widthMeasureSpec);
            int maximumHeight = MeasureSpec.getSize(heightMeasureSpec);
            float scaleW = (float) movieWidth / (float) maximumWidth;
            mScaleW = 1f / scaleW;
            mMeasuredMovieWidth = maximumWidth;
            float scaleH = (float) movieHeight / (float) maximumHeight;
            mScaleH = 1f / scaleH;
            mMeasuredMovieHeight = maximumHeight;
            // mMeasuredMovieHeight = (int) (movieHeight * mScaleW);
            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight);
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mLeft2Screen = (getWidth() - mMeasuredMovieWidth) / 2f;
        mTop2Screen = (getHeight() - mMeasuredMovieHeight) / 2f;
        mVisible = getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mGifMovie != null) {
            if (!mPaused) {
                if (mLength > 0) {
                    updateAnimationTimeByLength();
                } else {
                    updateAnimationTime();
                }
            }
            drawMovieFrame(canvas);
            invalidateView();
        }
    }

    /**
     * 重绘
     */
    private void invalidateView() {
        if (mVisible) {
            invalidate();
        }
    }

    /**
     * 更新当前显示进度
     */
    private void updateAnimationTime() {
        int dur = mGifMovie.duration();
        // 取出动画的时长
        if (dur == 0) {
            dur = DEFAULT_MOVIE_DURATION;
        }
        long now = 0;
        if (0 < mDelay) {
            now = SystemClock.uptimeMillis() / mDelay;
        } else {
            now = SystemClock.uptimeMillis();
        }
        // 如果第一帧，记录起始时间
        if (mGifMovieStart == 0) {
            mGifMovieStart = now;
        }
        // 算出需要显示第几帧
        int next = (int) ((now - mGifMovieStart) % dur);
        if (mIsLoop || mCurrentDuration < next) {
            mCurrentDuration = next;
        }
    }

    /**
     * 更新当前显示进度
     */
    private void updateAnimationTimeByLength() {
        int dur = mGifMovie.duration();
        // 取出动画的时长
        if (dur == 0) {
            dur = DEFAULT_MOVIE_DURATION;
        }
        long now = SystemClock.uptimeMillis();
        // 如果第一帧，记录起始时间
        if (mGifMovieStart == 0) {
            mGifMovieStart = now;
        }
        // 一帧所需时间
        int onePic = mLength / dur;
        int next = 0;
        if (now > mGifMovieStart && onePic > 0) {
            next = (int) ((now - mGifMovieStart) / onePic);
        }
        if (next > dur) {
            next = 0;
            mGifMovieStart = now;
        }
        if (mIsLoop || mCurrentDuration < next) {
            mCurrentDuration = next;
        }
    }

    /**
     * 绘制图片
     * 
     * @param canvas 画布
     */
    private void drawMovieFrame(Canvas canvas) {
        mGifMovie.setTime(mCurrentDuration);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(mScaleW, mScaleH);
        mGifMovie.draw(canvas, mLeft2Screen / mScaleW, mTop2Screen / mScaleH);
        canvas.restore();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    /**
     * 将gif图片转换成byte[]
     * 
     * @return byte[]
     */

    private byte[] getGiftBytes(int imgId) {
        return getGiftBytes(imgId, null);
    }

    private byte[] getGiftBytes(String imgPath) {
        return getGiftBytes(-1, imgPath);
    }

    private byte[] getGiftBytes(int imgId, String imgPath) {
        ByteArrayOutputStream baos = null;
        InputStream is = null;

        byte[] b = new byte[1024];
        int len;
        try {
            if (-1 != imgId) {
                is = getResources().openRawResource(imgId);
            } else if (null != imgPath && !"".equals(imgPath.trim())) {
                is = new FileInputStream(new File(imgPath));
            }
            baos = new ByteArrayOutputStream();
            while (null != is && (len = is.read(b, 0, 1024)) != -1) {
                baos.write(b, 0, len);
            }
            baos.flush();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return baos == null ? null : baos.toByteArray();
    }
}
