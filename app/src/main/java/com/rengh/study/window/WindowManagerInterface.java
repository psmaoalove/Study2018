package com.rengh.study.window;

/**
 * Created by rengh on 2018/1/27.
 */
public interface WindowManagerInterface {
    /**
     * 判断WindowMananger是否为空
     *
     * @return true 或 false
     */
    boolean isFinishing();

    /**
     * 结束WindowManager
     */
    void finish();
}
