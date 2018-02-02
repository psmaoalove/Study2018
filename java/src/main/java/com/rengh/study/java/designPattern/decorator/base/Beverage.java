package com.rengh.study.java.designPattern.decorator.base;

/**
 * Created by rengh on 18-2-2.
 */

public abstract class Beverage {
    public String description = "Unknow beverage";

    public String getDescription() {
        return description;
    }

    public abstract double cost();
}
