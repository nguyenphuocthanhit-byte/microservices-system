package com.example.demo.job;

import com.example.demo.entity.PendingMessage;
import com.example.demo.repository.PendingMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PendingMessageRecoveryJob {

    @Autowired
    private PendingMessageRepository repo;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // Chạy mỗi 30s để gửi lại message chưa gửi được
    @Scheduled(fixedDelay = 30000)
    public void resendPendingMessages() {
        List<PendingMessage> messages = repo.findBySentFalse();
        for (PendingMessage msg : messages) {
            try {
                rabbitTemplate.convertAndSend(msg.getExchangeName(), msg.getRoutingKey(), msg.getPayload());
                msg.setSent(true);
                repo.save(msg);
                log.info("🔁 Resent message: " + msg.getId());
            } catch (Exception e) {
                log.info("❌ Still failed to send message " + msg.getId() + ": " + e.getMessage());
            }
        }
    }
}

