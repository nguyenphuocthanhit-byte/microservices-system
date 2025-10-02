package com.example.demo.service;

import com.example.event.UserCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RabbitMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @RabbitListener(queues = "user_queue")
    public void consumeUserCreationEvent(UserCreatedEvent user) {
        LOGGER.info("Received user creation event: {}", user);

        // Xử lý logic khi có user mới được tạo
        // Ví dụ: tạo cart cho user, gửi email chào mừng, etc.
        processNewUser(user);
    }

    private void processNewUser(UserCreatedEvent user) {
        // Logic xử lý khi có user mới
        LOGGER.info("Processing new user: " + user.getName() + " with email: " + user.getEmail());

        // Ví dụ: Tạo cart mặc định cho user
        createDefaultCartForUser(user.getId());

        // Ví dụ: Gửi email chào mừng
        sendWelcomeEmail(user);
    }

    private void createDefaultCartForUser(Long userId) {
        LOGGER.info("Creating default cart for user ID: " + userId);
        // Logic tạo cart
    }

    private void sendWelcomeEmail(UserCreatedEvent user) {
        LOGGER.info("Sending welcome email to: " + user.getEmail());
        // Logic gửi email
    }
}