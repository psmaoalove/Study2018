package com.rengh.study.java.designPattern.strategy.quack;

/**
 * Created by rengh on 18-1-24.
 */

public class QuackZiziImpl implements QuackBehavior {
    @Override
    public void quack() {
        System.out.println("Zi~Zi~");
    }
}
