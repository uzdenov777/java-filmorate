package ru.yandex.practicum.filmorate.storage.interfaces;

public interface FilmGenresStorage {

    void addFilmGenre(Long filmId, Integer genreId);

    void deleteFilmGenreByFilmId(Long filmId);
}
