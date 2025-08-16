package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmGenresStorage {
    void add(Long filmId, Integer genreId);

    void update(Long filmId, Integer genreId);

//    List<User> getAllUsers();
//
//    User getUserById(long id);
}
