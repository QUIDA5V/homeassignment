package com.sport.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class ScheduledService {

    @Value("${client.api.period}")
    private long clientPeriod;

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;

    private final WebClient webClient;
    private final EventProducerService eventProducerService;

    private final Map<String, ScheduledFuture<?>> tasks;// = new ConcurrentHashMap<>();

    public ScheduledService(WebClient webClient, EventProducerService eventProducerService, TaskScheduler taskScheduler,Map<String, ScheduledFuture<?>> tasks) {
        this.webClient = webClient;
        this.eventProducerService = eventProducerService;
        this.taskScheduler = taskScheduler;
        this.tasks = tasks;
    }

    public void startTask(UUID eventId) {
        log.info("{}#{} start - startTask: {}", this.getClass().getSimpleName(), "startTask", eventId);

        scheduledFuture = tasks.get(eventId.toString());
        if (scheduledFuture == null || scheduledFuture.isCancelled()) {
            scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
                log.info("{}#{} calling EventAPI for EventId: {}", this.getClass().getSimpleName(), "startTask", eventId);

                try{
                    String scoreResponse =
                            webClient.get()
                                    .uri("/event/{eventId}", eventId)
                                    .accept(MediaType.TEXT_PLAIN)
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .block();

                    log.info("{}#{} response from EventAPI for EventId: {}:{}", this.getClass().getSimpleName(), "startTask", eventId, scoreResponse);

                    tasks.put(eventId.toString(), scheduledFuture);
                    eventProducerService.sendMessage(scoreResponse, eventId);
                } catch (WebClientResponseException.NotFound e) {
                    log.error("{}#{} ❌Error - Resource not found: {}", this.getClass().getSimpleName(), "calling webclient",  e.getResponseBodyAsString());
                } catch (WebClientResponseException e) {
                    log.error("{}#{} ❌Error - HTTP error: {}", this.getClass().getSimpleName(), "calling webclient",  e.getStatusCode());
                } catch (WebClientRequestException e) {
                    log.error("{}#{} ❌Error - Client error: {}", this.getClass().getSimpleName(), "calling webclient",  e.getMessage());
                } catch (Exception e){
                    log.error("{}#{} ❌Error - Error: {}", this.getClass().getSimpleName(), "startTask",  e.getMessage());
                }
            }, clientPeriod);
        }
        log.info("{}#{} end - startTask: {}", this.getClass().getSimpleName(), "startTask", eventId);
    }

    public void stopTask(UUID eventId) {
        log.info("{}#{} start - stopTask: {}", this.getClass().getSimpleName(), "stopTask", eventId);
        ScheduledFuture<?> scheduledFuture = tasks.get(eventId.toString());

        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
            tasks.remove(eventId.toString());
            log.info("{}#{} start - removed task with EventId: {}", this.getClass().getSimpleName(), "stopTask", eventId);
        }

        log.info("{}#{} end - stopTask: {}", this.getClass().getSimpleName(), "stopTask", eventId);
    }
}