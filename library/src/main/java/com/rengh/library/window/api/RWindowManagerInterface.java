package com.rengh.library.window.api;

/**
 * Created by rengh on 2018/1/27.
 */
public interface RWindowManagerInterface {
    /**
     * 判断WindowMananger是否为空
     *
     * @return true 或 false
     */
    boolean isReleased();

    /**
     * 结束WindowManager
     */
    void release();

    void setListiner(RWindowListiner listiner);
}
