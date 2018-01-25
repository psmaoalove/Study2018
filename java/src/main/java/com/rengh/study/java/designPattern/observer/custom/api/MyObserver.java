package com.rengh.study.java.designPattern.observer.custom.api;

/**
 * 观察者接口
 * Created by rengh on 18-1-25.
 */
public interface MyObserver {
    /**
     * 观察者被通知时的回调方法
     *
     * @param myObserverable 被观察的对象
     * @param object         附加数据
     */
    void update(MyObserverable myObserverable, Object object);
}
