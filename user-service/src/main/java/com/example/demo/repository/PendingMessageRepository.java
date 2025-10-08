package com.example.demo.repository;


import com.example.demo.entity.PendingMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PendingMessageRepository extends JpaRepository<PendingMessage, Long> {
    List<PendingMessage> findBySentFalse();
}