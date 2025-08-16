package ru.yandex.practicum.filmorate.storage.interfaces;

public interface FilmLikesStorage {
    void add(Long filmId, Long userId);

    void update(Long filmId, Long userId);

}