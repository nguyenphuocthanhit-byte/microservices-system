package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.entity.PendingMessage;
import com.example.demo.entity.User;
import com.example.demo.repository.PendingMessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RabbitMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PendingMessageRepository pendingRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @CircuitBreaker(name = "rabbitMqCB", fallbackMethod = "fallbackSend")
    public void sendUserCreationEvent(Object user) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                user
        );
        System.out.println("User creation event sent: " + user);
    }


    public void fallbackSend(User user, Throwable t) throws JsonProcessingException {
        System.out.println("⚠️ MQ unavailable! Saving message to pending table. Reason: " + t.getMessage());
        log.error("⚠️ MQ unavailable! Saving message to pending table. Reason: ", t.getMessage());

        String payload = objectMapper.writeValueAsString(user);
        PendingMessage msg = PendingMessage.builder()
                .payload(payload)
                .exchangeName(RabbitMQConfig.EXCHANGE_NAME)
                .routingKey(RabbitMQConfig.ROUTING_KEY)
                .build();
        pendingRepo.save(msg);
    }
}