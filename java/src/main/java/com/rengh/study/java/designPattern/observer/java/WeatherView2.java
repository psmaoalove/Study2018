package com.rengh.study.java.designPattern.observer.java;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by rengh on 18-1-25.
 */

public class WeatherView2 implements Observer {
    public WeatherView2() {
    }

    public void addObserver(Observable observable) {
        observable.addObserver(this);
    }

    public void deleteObserver(Observable observable) {
        observable.deleteObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof WeatherData) {
            WeatherData weatherData = (WeatherData) observable;
            System.out.println("2 FengLi: " + weatherData.getFengLi());
            System.out.println("2 ShiDu: " + weatherData.getShiDu());
            System.out.println("2 WenDu: " + weatherData.getWenDu());
        }
    }
}
