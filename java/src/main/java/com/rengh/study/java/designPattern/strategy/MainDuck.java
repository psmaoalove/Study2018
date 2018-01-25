package com.rengh.study.java.designPattern.strategy;

import com.rengh.study.java.designPattern.strategy.bean.BlackDuck;
import com.rengh.study.java.designPattern.strategy.bean.YellowDuck;
import com.rengh.study.java.designPattern.strategy.fly.FlyLowAltitudeImpl;
import com.rengh.study.java.designPattern.strategy.fly.FlyNoWayImpl;
import com.rengh.study.java.designPattern.strategy.quack.QuackGuaguaImpl;
import com.rengh.study.java.designPattern.strategy.quack.QuackZiziImpl;

public class MainDuck {
    public static void main(String[] args) {
        YellowDuck yellowDuck = new YellowDuck();
        BlackDuck blackDuck = new BlackDuck();

        yellowDuck.display();
        yellowDuck.swim();
        yellowDuck.setFlyBehavior(new FlyNoWayImpl());
        yellowDuck.setQuackBehavior(new QuackZiziImpl());
        yellowDuck.fly();
        yellowDuck.quack();

        blackDuck.display();
        blackDuck.swim();
        blackDuck.setFlyBehavior(new FlyLowAltitudeImpl());
        blackDuck.setQuackBehavior(new QuackGuaguaImpl());
        blackDuck.fly();
        blackDuck.quack();
    }
}
