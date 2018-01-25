package com.rengh.study.java.designPattern.strategy.fly;

/**
 * Created by rengh on 18-1-24.
 */

public class FlyNoWayImpl implements FlyBehavior {
    @Override
    public void fly() {
        // Nothing.
        // Can not fly.
        System.out.println("Can not fly.");
    }
}
