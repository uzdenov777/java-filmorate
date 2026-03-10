package ru.yandex.practicum.filmorate.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;

import java.time.LocalDateTime;

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
