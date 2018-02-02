package com.rengh.study.java.designPattern.decorator.beverage;

import com.rengh.study.java.designPattern.decorator.base.Beverage;

/**
 * Created by rengh on 18-2-2.
 */

public class CoffeeBeverage extends Beverage {
    public CoffeeBeverage() {
        description = "CoffeeBeverage";
    }

    @Override
    public double cost() {
        return 2.08;
    }
}
