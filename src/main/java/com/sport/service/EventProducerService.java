package com.sport.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class EventProducerService {

    @Value("${kafka.topic.event}")
    private String eventTopic;

    @Value("${kafka.topic.event.maxRetries}")
    private int maxRetries;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public EventProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message, UUID eventId) {
        log.info("{}#{} start - sendingMessage: {}", this.getClass().getSimpleName(), "sendMessage", eventId);

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                kafkaTemplate.send(eventTopic, message).get();
                log.info("{}#{} ✅Message sent successfully: {}", this.getClass().getSimpleName(), "sendMessage", eventId);
                break;
            } catch (Exception e) {
                log.error("{}#{} ❌Error - sendingMessage {} - retry: {}", this.getClass().getSimpleName(), "sendMessage",eventId ,(attempt + 1));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        log.info("{}#{} end - sendingMessage: {}", this.getClass().getSimpleName(), "sendMessage", eventId);
    }
}