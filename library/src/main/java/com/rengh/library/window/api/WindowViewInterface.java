package com.rengh.library.window.api;

import android.view.View;
import android.view.WindowManager;

/**
 * Created by rengh on 2018/1/27.
 */
public interface WindowViewInterface {
    /**
     * 获取Params
     *
     * @return params
     */
    WindowManager.LayoutParams getParams();

    /**
     * 获取View
     *
     * @return view
     */
    View getView();

    /**
     * 初始化view
     */
    void initialize();

    /**
     * 释放资源
     */
    void release();

    /**
     * 是否已经释放资源
     *
     * @return true or false
     */
    boolean isReleased();

    WindowViewInterface setListiner(WindowListiner listiner);
}
