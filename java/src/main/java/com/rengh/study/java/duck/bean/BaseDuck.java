package com.rengh.study.java.duck.bean;

import com.rengh.study.java.duck.fly.FlyBehavior;
import com.rengh.study.java.duck.quack.QuackBehavior;

/**
 * Created by rengh on 18-1-24.
 */

public abstract class BaseDuck {
    public FlyBehavior flyBehavior;
    public QuackBehavior quackBehavior;

    public void swim() {
        System.out.println("Swimming...");
    }

    public abstract void display();

    public void setFlyBehavior(FlyBehavior flyBehavior) {
        this.flyBehavior = flyBehavior;
    }

    public void setQuackBehavior(QuackBehavior quackBehavior) {
        this.quackBehavior = quackBehavior;
    }

    public void fly() {
        if (null != flyBehavior) {
            flyBehavior.fly();
        } else {
            System.out.println("Unrealized!");
        }
    }

    public void quack() {
        if (null != quackBehavior) {
            quackBehavior.quack();
        } else {
            System.out.println("Unrealized!");
        }
    }

}
