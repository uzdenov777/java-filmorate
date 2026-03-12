package ru.yandex.practicum.filmorate.event.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.filmorate.event.enums.EventType;
import ru.yandex.practicum.filmorate.event.enums.Operation;
import ru.yandex.practicum.filmorate.user.model.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "entity_Id")
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation")
    private Operation operation;

    @Column(name = "timestamp")
    private Long timestamp;
}