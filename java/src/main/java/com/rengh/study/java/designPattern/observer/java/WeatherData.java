package com.rengh.study.java.designPattern.observer.java;

import java.util.Observable;

/**
 * Created by rengh on 18-1-25.
 */

public class WeatherData extends Observable {
    private double wenDu = 0;
    private double shiDu = 0;
    private double fengLi = 0;

    public double getWenDu() {
        return wenDu;
    }

    public void setWenDu(double wenDu) {
        this.wenDu = wenDu;
    }

    public double getShiDu() {
        return shiDu;
    }

    public void setShiDu(double shiDu) {
        this.shiDu = shiDu;
    }

    public double getFengLi() {
        return fengLi;
    }

    public void setFengLi(double fengLi) {
        this.fengLi = fengLi;
    }

    public void setChanged() {
        super.setChanged();
    }
}
