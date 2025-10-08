package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "pending_message")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PendingMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String payload;         // JSON hoặc text message
    private String exchangeName;
    private String routingKey;
    private boolean sent = false;   // đánh dấu đã gửi lại chưa
    private LocalDateTime createdAt = LocalDateTime.now();

}
