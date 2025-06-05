package com.sport.service;

import com.sport.dto.EventRequestDTO;
import com.sport.dto.EventStatus;
import com.sport.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class EventService {

    private final Map<UUID, EventStatus> memoryEventStatus;
    private final ScheduledService scheduledService;

    public EventService(ScheduledService scheduledService ,Map<UUID,EventStatus> memoryEventStatus) {
        this.scheduledService = scheduledService;
        this.memoryEventStatus = memoryEventStatus;

    }

    public void processEvent(EventRequestDTO eventRequestDTO) {
        log.info("{}#{} start - eventRequestDTO: {}", this.getClass().getSimpleName(), "processEvent", eventRequestDTO);
        log.info("{}#{} Status: {}", this.getClass().getSimpleName(), "processEvent", eventRequestDTO.getStatus());

        memoryEventStatus.put(eventRequestDTO.getEventId(), eventRequestDTO.getStatus());
        if (eventRequestDTO.getStatus().name().equals("LIVE")){
            scheduledService.startTask(eventRequestDTO.getEventId());
        } else {
            scheduledService.stopTask(eventRequestDTO.getEventId());
        }

        log.info("{}#{} end - eventRequestDTO: {}", this.getClass().getSimpleName(), "processEvent", eventRequestDTO);
    }

    public EventStatus getStatus(UUID eventId){
        EventStatus eventStatus = memoryEventStatus.get(eventId);
        if (eventStatus == null) {
            log.error("{}#{} ❌ResourceNotFound EventId: {}", this.getClass().getSimpleName(), "getStatus", eventId);
            throw new ResourceNotFoundException("Event not found : " + eventId);
        }
        return eventStatus;
    }
}