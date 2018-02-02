package com.rengh.study.java.designPattern.factory;

import com.rengh.study.java.designPattern.factory.pizza.base.Pizza;
import com.rengh.study.java.designPattern.factory.pizza.base.PizzaStore;
import com.rengh.study.java.designPattern.factory.pizza.store.cicago.CicagoPizzaStore;
import com.rengh.study.java.designPattern.factory.pizza.type.HamPizza;

/**
 * 工厂模式
 * Created by rengh on 18-2-2.
 */
public class MainClass {
    public static void main(String[] args) {
        PizzaStore pizzaStore = new CicagoPizzaStore();
        Pizza pizza = pizzaStore.orderPizza("ham");
        HamPizza hamPizza = (HamPizza) pizzaStore.orderPizza("ham");
        System.out.println(hamPizza.getPropties());
    }
}
