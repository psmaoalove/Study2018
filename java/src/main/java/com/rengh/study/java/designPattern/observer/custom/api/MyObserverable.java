package com.rengh.study.java.designPattern.observer.custom.api;

/**
 * 被观察者父类
 * Created by rengh on 18-1-25.
 */
public interface MyObserverable {
    /**
     * 添加观察者
     *
     * @param myObserver 观察者
     */
    void addObserver(MyObserver myObserver);

    /**
     * 移除观察者
     *
     * @param myObserver 观察者
     */
    void deleteObserver(MyObserver myObserver);

    /**
     * 数据改变标记，通知观察者之前必须调用。可在数据变化达到某一特定范围时通知观察者。
     */
    void setChanged();

    /**
     * 设置通知模式，正向 或 反向
     *
     * @param mode 模式
     */
    void setNotifyMode(final int mode);

    /**
     * 通知观察者
     */
    void notifyObservers();

    /**
     * 通知观察者
     *
     * @param object 附加对象
     */
    void notifyObservers(final Object object);
}
