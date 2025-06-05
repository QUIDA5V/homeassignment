package com.sport.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> uriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private EventProducerService eventProducerService;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private Map<String, ScheduledFuture<?>> tasks;

    @InjectMocks
    private ScheduledService scheduledService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(scheduledService, "clientPeriod", 1000);
        scheduledService = new ScheduledService(webClient, eventProducerService, taskScheduler, tasks);
    }

    @Test
    void shouldStartNewTask_WhenNoneExists() {
        UUID eventId = UUID.randomUUID();

        ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);

        when(tasks.get(eventId.toString())).thenReturn(null);
        when(taskScheduler.scheduleAtFixedRate(any(Runnable.class), any(Long.class))).thenReturn(scheduledFuture);

        scheduledService.startTask(eventId);
        verify(taskScheduler, times(1)).scheduleAtFixedRate(any(Runnable.class), any(Long.class));
    }

    @Test
    void shouldStopRunningTask_WhenExistsAndNotCancelled() {
        UUID eventId = UUID.randomUUID();
        ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);
        when(tasks.get(eventId.toString())).thenReturn(scheduledFuture);
        when(scheduledFuture.isCancelled()).thenReturn(false);

        scheduledService.stopTask(eventId);

        verify(scheduledFuture, times(1)).cancel(true);
        verify(tasks, times(1)).remove(eventId.toString());
    }

    @Test
    @DisplayName("Should Not Stop Task When Event Already Cancelled")
    void shouldNotStopTask_WhenAlreadyCancelled() {
        UUID eventId = UUID.randomUUID();
        ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);
        when(tasks.get(eventId.toString())).thenReturn(scheduledFuture);
        when(scheduledFuture.isCancelled()).thenReturn(true);

        scheduledService.stopTask(eventId);

        verify(scheduledFuture, never()).cancel(true);
        verify(tasks, never()).remove(anyString());
    }
}