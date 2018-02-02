package com.rengh.study.java.designPattern.decorator;

import com.rengh.study.java.designPattern.decorator.base.Beverage;
import com.rengh.study.java.designPattern.decorator.beverage.CoffeeBeverage;
import com.rengh.study.java.designPattern.decorator.condiment.MilkCondiment;
import com.rengh.study.java.designPattern.decorator.condiment.SugarCondiment;

/**
 * 装饰者模式
 * 装饰者模式动态地将责任附加到对象上。
 * 若要扩展功能，装饰者提供了比继承更有弹性
 * 的替代方案。
 * Created by rengh on 18-2-2.
 */
public class MainClass {
    public static void main(String[] args) {
        Beverage beverage = new CoffeeBeverage();
        beverage = new SugarCondiment(beverage);
        beverage = new SugarCondiment(beverage);
        beverage = new MilkCondiment(beverage);
        System.out.println(beverage.getDescription() + ", $: " + beverage.cost());
    }
}
