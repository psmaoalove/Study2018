package com.rengh.study.java.designPattern.factory.pizza.base;

/**
 * Created by rengh on 18-2-2.
 */

public abstract class PizzaStore {
    public Pizza orderPizza(String type) {
        Pizza pizza = createPizza(type);
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();
        return pizza;
    }

    public abstract Pizza createPizza(String type);
}
