package com.rengh.study.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.rengh.study.util.common.LogUtils;
import com.rengh.study.util.common.ServiceUtils;

import java.util.HashMap;
import java.util.Map;

public class MyAccessibilityService extends AccessibilityService {
    private final String TAG = "AppService";

    private Map<Integer, Boolean> handledMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.v(TAG, "===== onCreate() =====");
        ServiceUtils.startForeground(this, 1);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        LogUtils.v(TAG, "===== onStart() =====");
        // TODO
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.v(TAG, "===== onStartCommand() =====");
        onStart(intent, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "===== onDestroy() =====");
        ServiceUtils.stopForeground(this, false);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
            int eventType = event.getEventType();
            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                    eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (handledMap.get(event.getWindowId()) == null) {
                    boolean handled = iterateNodesAndHandle(nodeInfo);
                    if (handled) {
                        handledMap.put(event.getWindowId(), true);
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    private boolean iterateNodesAndHandle(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            int childCount = nodeInfo.getChildCount();
            if ("android.widget.Button".equals(nodeInfo.getClassName())) {
                String nodeContent = nodeInfo.getText().toString();
                LogUtils.d(TAG, "content is " + nodeContent);
                if ("安装".equals(nodeContent)
                        || "完成".equals(nodeContent)
                        || "确定".equals(nodeContent)) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            } else if ("android.widget.ScrollView".equals(nodeInfo.getClassName())) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (iterateNodesAndHandle(childNodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

}
