package com.rengh.study.window;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by rengh on 18-1-26.
 */

public class ExoWindowManager implements WindowManagerInterface {
    private static ExoWindowManager sInstance;
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private View view;
    private WindowViewInterface windowView;

    public ExoWindowManager(Context context) {
        this.context = context;
    }

    public static synchronized ExoWindowManager getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new ExoWindowManager(context);
        }
        return sInstance;
    }

    @Override
    public boolean isFinishing() {
        return null == windowManager;
    }

    @Override
    public void finish() {
        windowView.releasePlayer();
        windowManager.removeView(view);
        view.destroyDrawingCache();
        view = null;
        windowManager = null;
    }

    public void openWindow() {
        if (null == windowManager) {
            //获取WindowManager实例
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        windowView = new ExoPlayerWindowView(context, this);
        params = windowView.getParams();
        view = windowView.getView();

        //显示窗口
        windowManager.addView(view, params);

        windowView.initializePlayer();
    }

}
