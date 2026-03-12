package ru.yandex.practicum.filmorate.event.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.event.enums.EventType;
import ru.yandex.practicum.filmorate.event.enums.Operation;

@Data
public class EventDto {

    @JsonProperty("eventId")
    private Long id;

    private Long userId;

    private Long entityId;

    @JsonProperty("eventType")
    private EventType type;

    private Operation operation;

    private Long timestamp;
}