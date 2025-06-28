package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UserStorage {

    User add(User user);

    User update(User user);

    List<User> getAllUsers();

    User getUserById(long id);
}