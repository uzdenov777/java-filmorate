package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.EventDto;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    public void save(User user, Long entityId, EventType type, Operation operation) {
        Event event = new Event();
        event.setUser(user);
        event.setEntityId(entityId);
        event.setType(type);
        event.setOperation(operation);
        event.setTimestamp(Instant.now().toEpochMilli());

        eventRepository.save(event);
    }

    public Set<EventDto> findByUserId(Long id) {
       List<Event> events = eventRepository.findByUserId(id);

       return eventMapper.toDtos(events);
    }
}
