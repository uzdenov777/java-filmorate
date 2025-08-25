package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenresStorage {

    void addFilmGenre(Long filmId, Integer genreId);

    void deleteFilmGenresByFilmId(Long filmId);

    List<Genre> getGenresByFilmId(Long filmId);
}