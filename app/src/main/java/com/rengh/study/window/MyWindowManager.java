package com.rengh.study.window;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.rengh.study.window.api.WindowListiner;
import com.rengh.study.window.api.WindowManagerInterface;
import com.rengh.study.window.api.WindowViewInterface;

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
    private WindowListiner listiner;

    public MyWindowManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized MyWindowManager getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new MyWindowManager(context);
        }
        return sInstance;
    }

    @Override
    public void setListiner(WindowListiner listiner) {
        this.listiner = listiner;
    }

    @Override
    public boolean isReleased() {
        return null == windowManager;
    }

    @Override
    public void release() {
        if (null != windowManager) {
            windowManager.removeView(view);
            windowManager = null;
        }
        if (null != windowView) {
            windowView.release();
            windowView = null;
        }
        view = null;
    }

    public void openWindow(WindowViewInterface windowView) {
        if (null == windowManager) {
            //获取WindowManager实例
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }

        this.windowView = windowView;
        params = windowView.getParams();
        view = windowView.getView();

        this.windowView.setListiner(listiner);

        //显示窗口
        windowManager.addView(view, params);

        windowView.initialize();
    }

}
