package com.rengh.study.window;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by rengh on 18-1-26.
 */

public class MyWindowManager implements WindowManagerInterface {
    private static MyWindowManager sInstance;
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private View view;
    private WindowViewInterface windowView;

    public MyWindowManager(Context context) {
        this.context = context;
    }

    public static synchronized MyWindowManager getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new MyWindowManager(context);
        }
        return sInstance;
    }

    @Override
    public boolean isFinishing() {
        return null == windowManager;
    }

    @Override
    public void finish() {
        windowView.release();
        windowManager.removeView(view);
        view.destroyDrawingCache();
        view = null;
        windowManager = null;
    }

    public void openWindow(WindowViewInterface windowView) {
        if (null == windowManager) {
            //获取WindowManager实例
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        this.windowView = windowView;
        params = windowView.getParams();
        view = windowView.getView();

        //显示窗口
        windowManager.addView(view, params);

        windowView.initialize();
    }

}
