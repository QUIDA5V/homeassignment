package com.sport.service;

import com.sport.dto.EventRequestDTO;
import com.sport.dto.EventStatus;
import com.sport.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;

    @Mock
    private Map<UUID, EventStatus> memoryEventStatus;

    @Mock
    private ScheduledService scheduledService;

    @Test
    @DisplayName("Should return Event Status When Exist")
    void shouldReturnEventStatus_WhenExist() {
        UUID eventId = UUID.randomUUID();
        when(memoryEventStatus.get(eventId)).thenReturn(EventStatus.LIVE);

        EventStatus actualStatus = eventService.getStatus(eventId);
        assertEquals(actualStatus, EventStatus.LIVE);
    }

    @Test
    @DisplayName("Should return Error When Event not exist")
    void shouldReturnEventStatus_Error() {
        UUID eventId = UUID.randomUUID();
        assertThrows(ResourceNotFoundException.class, () -> eventService.getStatus(eventId));
    }

    @Test
    @DisplayName("Should Process LIVE Event Status")
    void shouldProcessEvent_whenIsLive() {
        UUID eventId = UUID.randomUUID();
        EventRequestDTO eventRequestDTO = new EventRequestDTO();
        eventRequestDTO.setEventId(eventId);
        eventRequestDTO.setStatus(EventStatus.LIVE);

        doNothing().when(scheduledService).startTask(eventId);
        eventService.processEvent(eventRequestDTO);
        verify(scheduledService, times(1)).startTask(eventId);
    }

    @Test
    @DisplayName("Should Process NOT_LIVE Event Status")
    void shouldProcessEvent_whenIsNotLive() {
        UUID eventId = UUID.randomUUID();
        EventRequestDTO eventRequestDTO = new EventRequestDTO();
        eventRequestDTO.setEventId(eventId);
        eventRequestDTO.setStatus(EventStatus.NOT_LIVE);

        doNothing().when(scheduledService).stopTask(eventId);
        eventService.processEvent(eventRequestDTO);
        verify(scheduledService, times(1)).stopTask(eventId);
    }
}