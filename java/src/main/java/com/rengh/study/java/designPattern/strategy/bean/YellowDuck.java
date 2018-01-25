package com.rengh.study.java.designPattern.strategy.bean;

import com.rengh.study.java.designPattern.strategy.fly.FlyNoWayImpl;
import com.rengh.study.java.designPattern.strategy.quack.QuackZiziImpl;

/**
 * Created by rengh on 18-1-24.
 */

public class YellowDuck extends BaseDuck {
    public YellowDuck(){
        super.flyBehavior = new FlyNoWayImpl();
        super.quackBehavior = new QuackZiziImpl();
    }

    @Override
    public void display() {
        System.out.println("Yellow!");
    }
}
