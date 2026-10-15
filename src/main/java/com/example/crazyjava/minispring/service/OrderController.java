package com.example.crazyjava.minispring.service;

import com.example.crazyjava.minispring.annotation.MyAutowired;
import com.example.crazyjava.minispring.annotation.MyService;

@MyService
public class OrderController {
    @MyAutowired
    private OrderService orderService;

    public void printOrder() {
        System.out.println(orderService.getOrder());
    }
}
