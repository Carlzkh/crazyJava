package com.example.crazyjava.minispring.service;

import com.example.crazyjava.minispring.annotation.MyService;

@MyService
public class OrderService {
    public String getOrder() {
        return "订单 #001";
    }
}
