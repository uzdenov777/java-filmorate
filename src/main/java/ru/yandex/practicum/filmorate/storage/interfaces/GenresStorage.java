package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenresStorage {

    Genre getGenreById(int id);

    List<Genre> getAllGenres();
}