package com.rengh.study.java.designPattern.strategy.fly;

/**
 * Created by rengh on 18-1-24.
 */

public class FlyLowAltitudeImpl implements FlyBehavior {
    @Override
    public void fly() {
        System.out.println("Fly in low altitude.");
    }
}
