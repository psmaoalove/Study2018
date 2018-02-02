package com.rengh.study.java.designPattern.factory.pizza.store.cicago;

import com.rengh.study.java.designPattern.factory.pizza.base.Pizza;
import com.rengh.study.java.designPattern.factory.pizza.base.PizzaStore;
import com.rengh.study.java.designPattern.factory.pizza.type.CheesePizza;
import com.rengh.study.java.designPattern.factory.pizza.type.HamPizza;

/**
 * Created by rengh on 18-2-2.
 */
public class CicagoPizzaStore extends PizzaStore {
    @Override
    public Pizza createPizza(String type) {
        Pizza pizza = null;
        if ("ham".equalsIgnoreCase(type)) {
            pizza = new HamPizza();
        } else if ("cheese".equalsIgnoreCase(type)) {
            pizza = new CheesePizza();
        }
        return pizza;
    }
}
