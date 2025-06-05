package com.sport.controller;

import com.sport.dto.EventRequestDTO;
import com.sport.dto.EventStatus;
import com.sport.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    @PostMapping("/status")
    public ResponseEntity<?> processEvent(@Valid @RequestBody EventRequestDTO eventRequest) {
        log.info("{}#{} start - eventRequest: {}", this.getClass().getSimpleName(), "processEvent", eventRequest);
        eventService.processEvent(eventRequest);
        log.info("{}#{} end - eventRequest: {}", this.getClass().getSimpleName(), "processEvent", eventRequest);

        return ResponseEntity.ok("Event Processed");
    }

    @GetMapping("/status/{eventId}")
    public ResponseEntity<?> getEventStatus(@PathVariable UUID eventId) {
        log.info("{}#{} start - eventId: {}", this.getClass().getSimpleName(), "getEventStatus", eventId);
        EventStatus status = eventService.getStatus(eventId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        log.info("{}#{} end - eventId: {}", this.getClass().getSimpleName(), "getEventStatus", eventId);

        return ResponseEntity.ok(status.name());
    }
}