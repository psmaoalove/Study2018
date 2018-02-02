package com.rengh.study.java.designPattern.decorator.beverage;

import com.rengh.study.java.designPattern.decorator.base.Beverage;

/**
 * Created by rengh on 18-2-2.
 */

public class TeaBeverage extends Beverage {
    public TeaBeverage() {
        description = "TeaBeverage";
    }

    @Override
    public double cost() {
        return 1.68;
    }
}
