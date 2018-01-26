package com.rengh.study.java.designPattern.observer.custom;

import com.rengh.study.java.designPattern.observer.custom.api.MyObserver;
import com.rengh.study.java.designPattern.observer.custom.api.MyObserverable;

/**
 * 观察者模式
 * Created by rengh on 18-1-25.
 */

public class MainData {
    public static void main(String[] args) {
        DataClass dataClass = new DataClass();

        DisplayClass displayClass = new DisplayClass();
        Display2Class display2Class = new Display2Class();

        dataClass.addObserver(displayClass);
        dataClass.addObserver(display2Class);

        dataClass.addObserver(new MyObserver() {
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
                System.out.println("MyObserver-data1: " + data1);
                System.out.println("MyObserver-data2: " + data2);
            }
        });

        dataClass.setChanged();
        dataClass.notifyObservers();

        dataClass.setData1(1234567);
        dataClass.setData2(9876521);

        dataClass.setNotifyMode(DataClass.NOTIFY_MODE_REVERSE); // 反向通知
        dataClass.setChanged();
        dataClass.notifyObservers();
    }
}
