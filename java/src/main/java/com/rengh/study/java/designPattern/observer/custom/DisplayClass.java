package com.rengh.study.java.designPattern.observer.custom;

import com.rengh.study.java.designPattern.observer.custom.api.MyObserver;
import com.rengh.study.java.designPattern.observer.custom.api.MyObserverable;

/**
 * Created by rengh on 18-1-25.
 */

public class DisplayClass implements MyObserver {
    private int data1;
    private int data2;

    @Override
    public void update(MyObserverable myObserverable, Object object) {
        if (myObserverable instanceof DataClass) {
            DataClass dataClass = (DataClass) myObserverable;
            data1 = dataClass.getData1();
            data2 = dataClass.getData2();
            display();
        }
    }

    private void display() {
        System.out.println("DisplayClass-data1: " + data1);
        System.out.println("DisplayClass-data2: " + data2);
    }
}
