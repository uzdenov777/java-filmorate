package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UsersStorage {

    User add(User newUser);

    User update(User userToUser);

    List<User> getAllUsers();

    User getUserById(long userId);
}