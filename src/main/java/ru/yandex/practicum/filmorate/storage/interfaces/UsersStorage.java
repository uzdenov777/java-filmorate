package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;


public interface UsersStorage {

    User add(User newUser);

    User update(User userToUser);

    List<User> findAll();

    Optional<User> findById(long userId);
}