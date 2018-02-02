package com.rengh.study.java.designPattern.decorator.condiment;

import com.rengh.study.java.designPattern.decorator.base.Beverage;
import com.rengh.study.java.designPattern.decorator.base.CondimentBeverage;

/**
 * Created by rengh on 18-2-2.
 */

public class MilkCondiment extends CondimentBeverage {
    private Beverage beverage;

    public MilkCondiment(Beverage beverage) {
        this.beverage = beverage;
    }

    @Override
    public String getDescription() {
        return beverage.getDescription() + ", Milk";
    }

    @Override
    public double cost() {
        return 0.6 + beverage.cost();
    }
}
