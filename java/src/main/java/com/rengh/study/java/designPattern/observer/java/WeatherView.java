package com.rengh.study.java.designPattern.observer.java;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by rengh on 18-1-25.
 */

public class WeatherView implements Observer {
    public WeatherView() {
    }

    public void addObserver(Observable observable) {
        observable.addObserver(this);
    }

    public void deleteObserver(Observable observable) {
        observable.deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        WeatherData weatherData = (WeatherData) observable;
        System.out.println("FengLi: " + weatherData.getFengLi());
        System.out.println("ShiDu: " + weatherData.getShiDu());
        System.out.println("WenDu: " + weatherData.getWenDu());
    }
}
