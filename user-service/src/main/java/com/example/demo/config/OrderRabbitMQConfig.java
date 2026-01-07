package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

public class OrderRabbitMQConfig {
    public static final String ORDER_EXCHANGE_NAME = "order_exchange";
    public static final String ORDER_QUEUE_NAME = "";
    public static final String ORDER_ROUTING_KEY_CREATE = "order.create";
    public static final String ORDER_ROUTING_KEY_UPDATE = "order.create";

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE_NAME).build();
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE_NAME);
    }

    @Bean
    public Binding orderCreateBinding(Queue orderQueue, TopicExchange orderExchange) {
        return BindingBuilder
                .bind(orderQueue)
                .to(orderExchange)
                .with(ORDER_ROUTING_KEY_CREATE);
    }

    @Bean
    public Binding orderUpadteBinding(Queue orderQueue, TopicExchange orderExchange) {
        return BindingBuilder
                .bind(orderQueue)
                .to(orderExchange)
                .with(ORDER_ROUTING_KEY_UPDATE);
    }
}
