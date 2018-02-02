package com.rengh.study.java.designPattern.decorator.condiment;

import com.rengh.study.java.designPattern.decorator.base.Beverage;
import com.rengh.study.java.designPattern.decorator.base.CondimentBeverage;

/**
 * Created by rengh on 18-2-2.
 */

public class SugarCondiment extends CondimentBeverage {
    private Beverage beverage;

    public SugarCondiment(Beverage beverage) {
        this.beverage = beverage;
    }

    @Override
    public String getDescription() {
        return beverage.getDescription() + ", Sugar";
    }

    @Override
    public double cost() {
        return 0.4 + beverage.cost();
    }
}
