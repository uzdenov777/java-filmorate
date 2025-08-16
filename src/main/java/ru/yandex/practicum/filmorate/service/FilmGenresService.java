package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDbStorage;

import java.util.HashSet;
import java.util.Set;

@Service
public class FilmGenresService {
    FilmGenreDbStorage filmGenreDbStorage;

    @Autowired
    public FilmGenresService(FilmGenreDbStorage filmGenreDbStorage) {
        this.filmGenreDbStorage = filmGenreDbStorage;
    }

    public void addFilmGenres(Long film_id, Set<Genre> genres) {
        Set<Integer> genreIds = new HashSet<>();
        for (Genre genre : genres) {
            genreIds.add(genre.getId());
        }

        for (Integer genreId : genreIds) {
            filmGenreDbStorage.addFilmGenre(film_id, genreId);
        }
    }

    public void deleteAllFilmGenresByFilmId(Long film_id) {
        filmGenreDbStorage.deleteFilmGenreByFilmId(film_id);
    }

    public void updateFilmGenres(Long film_id, Set<Genre> genres) {
        filmGenreDbStorage.deleteFilmGenreByFilmId(film_id);
        addFilmGenres(film_id, genres);
    }
}
