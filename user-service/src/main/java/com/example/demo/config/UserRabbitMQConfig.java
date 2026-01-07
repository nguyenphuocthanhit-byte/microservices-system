package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

public class UserRabbitMQConfig {
    public static final String USER_EXCHANGE_NAME = "user_exchange";
    public static final String USER_QUEUE_NAME = "";
    public static final String USER_ROUTING_KEY = "user_routingKey";

    @Bean
    public Queue queue() {
        return new Queue(USER_QUEUE_NAME);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(USER_EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(USER_ROUTING_KEY);
    }

}
