package com.rengh.library.window.api;

/**
 * Created by rengh on 18-1-29.
 */

public interface RVideoListinerR extends RWindowListiner {
    void onPlayStart();

    void onPlayError(String err);

    void onPlayCompleted();

    void onPlayCompletedByUser();
}
