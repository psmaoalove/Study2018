package com.rengh.study.java.designPattern.observer.custom;

/**
 * Created by rengh on 18-1-25.
 */

public class MainData {
    public static void main(String[] args) {
        DataClass dataClass = new DataClass();

        DisplayClass displayClass = new DisplayClass();
        Display2Class display2Class = new Display2Class();

        dataClass.addObserver(displayClass);
        dataClass.addObserver(display2Class);

        dataClass.setChanged();
        dataClass.notifyObservers();

        dataClass.setData1(1234567);
        dataClass.setData2(9876521);

        dataClass.setNotifyMode(DataClass.NOTIFY_MODE_REVERSE); // 反向通知
        dataClass.setChanged();
        dataClass.notifyObservers();
    }
}
