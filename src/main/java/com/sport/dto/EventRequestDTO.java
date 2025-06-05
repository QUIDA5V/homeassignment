package com.sport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {
    @NotNull(message = "eventId is required")
    private UUID eventId;

    @NotNull(message = "status is required")
    private EventStatus status;
}