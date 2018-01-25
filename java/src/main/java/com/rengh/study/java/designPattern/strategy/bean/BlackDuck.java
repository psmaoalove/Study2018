package com.rengh.study.java.designPattern.strategy.bean;

import com.rengh.study.java.designPattern.strategy.fly.FlyLowAltitudeImpl;
import com.rengh.study.java.designPattern.strategy.quack.QuackGuaguaImpl;

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
