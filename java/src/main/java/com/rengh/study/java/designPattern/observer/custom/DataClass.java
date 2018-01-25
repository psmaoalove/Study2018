package com.rengh.study.java.designPattern.observer.custom;

import com.rengh.study.java.designPattern.observer.custom.api.MyObserverable;

/**
 * Created by rengh on 18-1-25.
 */

public class DataClass extends MyObserverable {
    private int data1;
    private int data2;

    public int getData1() {
        return data1;
    }

    public void setData1(int data1) {
        this.data1 = data1;
    }

    public int getData2() {
        return data2;
    }

    public void setData2(int data2) {
        this.data2 = data2;
    }
}
