package com.rengh.study.java.duck.quack;

/**
 * Created by rengh on 18-1-24.
 */

public class QuackGuaguaImpl implements QuackBehavior {
    @Override
    public void quack() {
        System.out.println("Gua! Gua!");
    }
}
