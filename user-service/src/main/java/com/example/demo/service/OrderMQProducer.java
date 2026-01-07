package com.example.demo.service;

import com.example.demo.config.OrderRabbitMQConfig;
import com.example.demo.config.UserRabbitMQConfig;
import com.example.demo.entity.PendingMessage;
import com.example.demo.entity.User;
import com.example.demo.repository.PendingMessageRepository;
import com.example.dto.OrderDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class OrderMQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PendingMessageRepository pendingRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @CircuitBreaker(name = "rabbitMqCB", fallbackMethod = "commonFallback")
    public void sendUserCreationEvent(Object user) {

  /*      rabbitTemplate.convertAndSend(
                UserRabbitMQConfig.USER_EXCHANGE_NAME,
                UserRabbitMQConfig.USER_ROUTING_KEY,
                user
        );*/
        rabbitTemplate.convertAndSend(
                UserRabbitMQConfig.USER_EXCHANGE_NAME,
                UserRabbitMQConfig.USER_ROUTING_KEY,
                user,
                message -> {
                    message.getMessageProperties()
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                }
        );
        System.out.println("User creation event sent: " + user);
    }

    @CircuitBreaker(name = "rabbitMqCB", fallbackMethod = "commonFallback")
    public void sendOrderCreationEvent(Object orderDTO, String routingKey) {

        rabbitTemplate.convertAndSend(
                OrderRabbitMQConfig.ORDER_EXCHANGE_NAME,
                routingKey,
                orderDTO
        );
        System.out.println("Order creation event sent: " + orderDTO);
    }

    // -------------------- COMMON FALLBACK --------------------
    public void commonFallback(Object message, String routingKey, Throwable t) throws JsonProcessingException {
        log.error("⚠️️ RabbitMQ unavailable! Saving to pending table. Reason: {}", t.getMessage());

        String payload = objectMapper.writeValueAsString(message);
        String exchangeName;

        // Xử lý riêng từng loại message
        if (message instanceof User) {
            exchangeName = UserRabbitMQConfig.USER_EXCHANGE_NAME;
            log.warn("Saving pending User message: {}", payload);
        } else if (message instanceof OrderDTO) {
            exchangeName = OrderRabbitMQConfig.ORDER_EXCHANGE_NAME;
            log.warn("Saving pending ORDER message: {}", payload);
        } else {
            exchangeName = "";
            log.warn("Saving pending UNKNOWN message type: {}", payload);
        }

        PendingMessage msg = PendingMessage.builder()
                .payload(payload)
                .exchangeName(exchangeName)
                .routingKey(routingKey)
                .build();

        pendingRepo.save(msg);
        log.info("💾 Pending message saved successfully to DB.");
    }
}