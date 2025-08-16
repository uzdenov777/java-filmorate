package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmLikesStorage {
    void add(Long filmId, Long userId);

    void update(Long filmId, Long userId);

    //    List<User> getAllUsers();
    //
    //    User getUserById(long id);
}