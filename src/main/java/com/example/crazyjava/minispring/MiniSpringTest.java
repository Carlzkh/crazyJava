package com.example.crazyjava.minispring;

import com.example.crazyjava.minispring.service.OrderController;

public class MiniSpringTest {
    public static void main(String[] args) throws Exception {
        MiniApplicationContext context =
                new MiniApplicationContext("com.example.crazyjava.minispring.service");

        OrderController controller = context.getBean(OrderController.class);
        controller.printOrder();
    }
}
