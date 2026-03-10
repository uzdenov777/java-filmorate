package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.dto.EventDto;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "userId", source = "user.id")
    EventDto toDto(Event event);

    Set<EventDto> toDtos(List<Event> events);
}
