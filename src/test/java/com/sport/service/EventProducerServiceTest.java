package com.sport.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventProducerServiceTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private EventProducerService eventProducerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eventProducerService, "maxRetries", 3);
        ReflectionTestUtils.setField(eventProducerService, "eventTopic", "test-topic");
    }

    @Test
    @DisplayName("Should send Message Successfully")
    void shouldSendMessage() {
        String message = "test-message";
        UUID eventId = UUID.randomUUID();

        SendResult<String, String> sendResult = mock(SendResult.class);

        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(sendResult));
        eventProducerService.sendMessage(message ,eventId);

        verify(kafkaTemplate, times(1)).send(anyString(), eq(message));
    }

    @Test
    @DisplayName("Should re attempt before succeed")
    void shouldReAttemptSendingMessage() throws Exception {
        String message = "test-message";
        UUID eventId = UUID.randomUUID();
        SendResult<String, String> sendResult = mock(SendResult.class);

        CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka send failed"));
        when(kafkaTemplate.send(anyString(), eq(message)))
                .thenReturn(failedFuture)
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        eventProducerService.sendMessage(message ,eventId);
        verify(kafkaTemplate, times(2)).send(anyString(), eq(message));
    }

    @Test
    @DisplayName("Should fail sending Messages")
    void shouldFailSendingMessage() throws Exception {
        String message = "test-message";
        UUID eventId = UUID.randomUUID();

        CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka send failed"));
        when(kafkaTemplate.send(anyString(), eq(message)))
                .thenReturn(failedFuture);
        eventProducerService.sendMessage(message ,eventId);
        verify(kafkaTemplate, times(3)).send(anyString(), eq(message));
    }
}