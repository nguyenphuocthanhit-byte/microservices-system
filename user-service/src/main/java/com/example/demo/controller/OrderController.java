package com.example.demo.controller;

import com.example.demo.config.OrderRabbitMQConfig;
import com.example.demo.service.OrderMQProducer;
import com.example.dto.OrderDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderMQProducer orderMQProducer;


    @PostMapping
    public ResponseEntity<OrderDTO> createUser(@Valid @RequestBody OrderDTO order) {
        log.info("Create user");
        orderMQProducer.sendOrderCreationEvent(order, OrderRabbitMQConfig.ORDER_ROUTING_KEY_CREATE);
        return ResponseEntity.ok(order);
    }
}
