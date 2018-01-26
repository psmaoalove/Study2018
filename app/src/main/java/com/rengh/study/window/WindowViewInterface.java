package com.rengh.study.window;

import android.view.View;
import android.view.WindowManager;

/**
 * Created by rengh on 2018/1/27.
 */

public interface WindowViewInterface {
    WindowManager.LayoutParams getParams();

    View getView();

    void initializePlayer();

    void releasePlayer();
}
