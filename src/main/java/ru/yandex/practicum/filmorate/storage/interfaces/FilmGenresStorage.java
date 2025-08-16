package ru.yandex.practicum.filmorate.storage.interfaces;

public interface FilmGenresStorage {
    void add(Long filmId, Integer genreId);

    void update(Long filmId, Integer genreId);
}
