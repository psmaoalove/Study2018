package com.rengh.study.application;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by rengh on 18-1-15.
 */
public class StudyApplication extends Application {
    private static final String TAG = "StudyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }
}
