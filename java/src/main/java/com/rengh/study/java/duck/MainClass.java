package com.rengh.study.java.duck;

import com.rengh.study.java.duck.bean.BlackDuck;
import com.rengh.study.java.duck.bean.YellowDuck;

public class MainClass {
    public static void main(String[] args) {
        YellowDuck yellowDuck = new YellowDuck();
        BlackDuck blackDuck = new BlackDuck();

        yellowDuck.display();
        yellowDuck.swim();
        yellowDuck.fly();
        yellowDuck.quack();

        blackDuck.display();
        blackDuck.swim();
        blackDuck.fly();
        blackDuck.quack();
    }
}
