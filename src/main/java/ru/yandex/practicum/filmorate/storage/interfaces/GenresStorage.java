package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenresStorage {

    List<Genre> getAllGenres();

    Genre getGenreById(int id);
}
