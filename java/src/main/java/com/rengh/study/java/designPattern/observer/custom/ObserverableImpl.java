package com.rengh.study.java.designPattern.observer.custom;

import com.rengh.study.java.designPattern.observer.custom.api.MyObserver;
import com.rengh.study.java.designPattern.observer.custom.api.MyObserverable;

import java.util.Vector;

/**
 * 被观察者父类
 * Created by rengh on 18-1-25.
 */
public class ObserverableImpl implements MyObserverable {
    private Vector<MyObserver> vectors;
    private boolean changed;
    private int mode;

    /**
     * 正向通知观察者
     */
    public static final int NOTIFY_MODE_POSITIVE = 1;
    /**
     * 反向通知观察者
     */
    public static final int NOTIFY_MODE_REVERSE = 2;

    public ObserverableImpl() {
        vectors = new Vector<>();
        changed = false;
        mode = NOTIFY_MODE_POSITIVE;
    }

    /**
     * 添加观察者
     *
     * @param myObserver 观察者
     */
    @Override
    public synchronized void addObserver(MyObserver myObserver) {
        if (!vectors.contains(myObserver)) {
            vectors.add(myObserver);
        }
    }

    /**
     * 移除观察者
     *
     * @param myObserver 观察者
     */
    @Override
    public synchronized void deleteObserver(MyObserver myObserver) {
        int index = vectors.indexOf(myObserver);
        if (index >= 0) {
            vectors.remove(index);
        }
    }

    /**
     * 数据改变标记，通知观察者之前必须调用。可在数据变化达到某一特定范围时通知观察者。
     */
    @Override
    public synchronized void setChanged() {
        changed = true;
    }

    /**
     * 设置通知模式，正向 或 反向
     *
     * @param mode 模式
     */
    @Override
    public synchronized void setNotifyMode(final int mode) {
        this.mode = mode;
    }

    /**
     * 通知观察者
     */
    @Override
    public synchronized void notifyObservers() {
        notifyObservers(null);
    }

    /**
     * 通知观察者
     *
     * @param object 附加对象
     */
    @Override
    public synchronized void notifyObservers(final Object object) {
        if (!changed) {
            return;
        }
        synchronized (this) {
            changed = false;
        }
        if (NOTIFY_MODE_POSITIVE == mode) {
            positiveNotify(object);
        } else if (NOTIFY_MODE_REVERSE == mode) {
            reverseNotify(object);
        } else {
            positiveNotify(object);
        }
    }

    /**
     * 正向通知算法
     *
     * @param object 附加数据
     */
    private void positiveNotify(final Object object) {
        for (int i = 0; i < vectors.size(); i++) {
            notify(i, object);
        }
    }

    /**
     * 反向通知算法
     *
     * @param object 附加数据
     */
    private void reverseNotify(final Object object) {
        for (int i = vectors.size() - 1; i >= 0; i--) {
            notify(i, object);
        }
    }

    /**
     * 通知观察者具体实现
     *
     * @param index  观察者坐标
     * @param object 附加数据
     */
    private void notify(final int index, final Object object) {
        MyObserver myObserver = vectors.get(index);
        if (null != myObserver) {
            myObserver.update(this, object);
        }
    }
}
