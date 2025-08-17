package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenresDbStorage;

import java.util.HashSet;
import java.util.Set;

@Service
public class FilmGenresService {
    FilmGenresDbStorage filmGenreDbStorage;

    @Autowired
    public FilmGenresService(FilmGenresDbStorage filmGenreDbStorage) {
        this.filmGenreDbStorage = filmGenreDbStorage;
    }

    public void addFilmGenres(Long filmId, Set<Genre> genres) {
        Set<Integer> genreIds = new HashSet<>();
        for (Genre genre : genres) {
            genreIds.add(genre.getId());
        }

        for (Integer genreId : genreIds) {
            filmGenreDbStorage.addFilmGenre(filmId, genreId);
        }
    }

    public void deleteAllFilmGenresByFilmId(Long filmId) {
        filmGenreDbStorage.deleteFilmGenreByFilmId(filmId);
    }

    public void updateFilmGenres(Long filmId, Set<Genre> genres) {
        filmGenreDbStorage.deleteFilmGenreByFilmId(filmId);
        addFilmGenres(filmId, genres);
    }
}