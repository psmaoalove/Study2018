package com.rengh.study.java.designPattern.observer.java;

/**
 * 观察者模式（JAVA自带的观察者模式）
 * Created by rengh on 18-1-25.
 */
public class MainObserver {
    public static void main(String[] args) {
        WeatherData weatherData = new WeatherData();

        WeatherView weatherView = new WeatherView();
        weatherView.addObserver(weatherData);
        WeatherView2 weatherView2 = new WeatherView2();
        weatherView2.addObserver(weatherData);

        weatherData.setFengLi(12.2);
        weatherData.setShiDu(2.36);
        weatherData.setWenDu(-11.5);
        weatherData.setChanged();
        weatherData.notifyObservers();
    }
}
