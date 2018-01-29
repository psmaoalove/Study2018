package com.rengh.study.window.api;

/**
 * Created by rengh on 18-1-29.
 */

public interface VideoWindowListiner extends WindowListiner {
    void onPlayStart();

    void onPlayError(String err);

    void onPlayCompleted();

    void onPlayCompletedByUser();
}
