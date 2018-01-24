package com.rengh.study.java.duck.bean;

import com.rengh.study.java.duck.fly.FlyNoWayImpl;
import com.rengh.study.java.duck.quack.QuackZiziImpl;

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
