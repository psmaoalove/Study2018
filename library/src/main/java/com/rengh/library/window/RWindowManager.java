package com.rengh.library.window;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.rengh.library.window.api.RWindowListiner;
import com.rengh.library.window.api.RWindowManagerInterface;
import com.rengh.library.window.api.RWindowViewInterface;

/**
 * Created by rengh on 18-1-26.
 */

public class RWindowManager implements RWindowManagerInterface {
    private static RWindowManager sInstance;
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private View view;
    private RWindowViewInterface windowView;
    private RWindowListiner listiner;

    public RWindowManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized RWindowManager getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new RWindowManager(context);
        }
        return sInstance;
    }

    @Override
    public void setListiner(RWindowListiner listiner) {
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

    public void openWindow(RWindowViewInterface windowView) {
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
