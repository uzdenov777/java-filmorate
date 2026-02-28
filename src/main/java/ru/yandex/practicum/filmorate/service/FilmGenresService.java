package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import jdk.jshell.Snippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmGenresRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FilmGenresService {

    private final FilmGenresRepository filmGenresRepository;

    @Autowired
    public FilmGenresService(FilmGenresRepository filmGenresRepository) {
        this.filmGenresRepository = filmGenresRepository;
    }

    public void addFilmGenres(Film film, List<Genre> genres) throws ResponseStatusException {

        try {
            Set<FilmGenre> filmGenres = new HashSet<>();

            for (Genre genre : genres) {

                FilmGenre filmGenre = new FilmGenre();
                filmGenre.setFilm(film);
                filmGenre.setGenre(genre);

                filmGenres.add(filmGenre);
            }

            filmGenresRepository.saveAll(filmGenres);

        } catch (DataIntegrityViolationException e) {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

//    public void deleteAllFilmGenresByFilm(Film film) {
//        filmGenresRepository.deleteFilmGenresByFilm(film);
//    }

    @Transactional
    public void updateFilmGenres(Film film, List<Genre> genres) {
        filmGenresRepository.deleteAllFilmGenresByFilm(film);
        addFilmGenres(film, genres);
    }

    public List<Genre> getGenresByFilm(Film film) {
        return new ArrayList<>(filmGenresRepository.getGenresByFilm(film));
    }
}