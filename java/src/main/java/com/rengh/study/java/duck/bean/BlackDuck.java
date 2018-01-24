package com.rengh.study.java.duck.bean;

import com.rengh.study.java.duck.fly.FlyLowAltitudeImpl;
import com.rengh.study.java.duck.quack.QuackGuaguaImpl;

/**
 * Created by rengh on 18-1-24.
 */

public class BlackDuck extends BaseDuck {
    public BlackDuck() {
        super.flyBehavior = new FlyLowAltitudeImpl();
        super.quackBehavior = new QuackGuaguaImpl();
    }

    @Override
    public void display() {
        System.out.println("Black!");
    }
}
