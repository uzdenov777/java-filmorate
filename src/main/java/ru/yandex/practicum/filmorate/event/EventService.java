package ru.yandex.practicum.filmorate.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.event.enums.EventType;
import ru.yandex.practicum.filmorate.event.enums.Operation;
import ru.yandex.practicum.filmorate.event.model.Event;
import ru.yandex.practicum.filmorate.event.model.dto.EventDto;
import ru.yandex.practicum.filmorate.user.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

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