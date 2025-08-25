package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenresDbStorage;

import java.util.HashSet;
import java.util.Set;

@Service
public class FilmGenresService {
    private final FilmGenresDbStorage filmGenreDbStorage;

    @Autowired
    public FilmGenresService(FilmGenresDbStorage filmGenreDbStorage) {
        this.filmGenreDbStorage = filmGenreDbStorage;
    }

    public void addFilmGenres(Long filmId, Set<Genre> genres) {
        for (Genre genre : genres) {
            Integer genreId = genre.getId();
            filmGenreDbStorage.addFilmGenre(filmId, genreId);
        }
    }

    public void deleteAllFilmGenresByFilmId(Long filmId) {
        filmGenreDbStorage.deleteFilmGenresByFilmId(filmId);
    }

    public void updateFilmGenres(Long filmId, Set<Genre> genres) {
        filmGenreDbStorage.deleteFilmGenresByFilmId(filmId);
        addFilmGenres(filmId, genres);
    }

    public Set<Genre> getGenresByFilmId(Long filmId) {
        return new HashSet<>(filmGenreDbStorage.getGenresByFilmId(filmId));
    }
}