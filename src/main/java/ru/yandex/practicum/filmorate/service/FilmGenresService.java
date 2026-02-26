//package ru.yandex.practicum.filmorate.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import ru.yandex.practicum.filmorate.model.Genre;
//import ru.yandex.practicum.filmorate.storage.interfaces.FilmGenresStorage;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Service
//public class FilmGenresService {
//
//    private final FilmGenresStorage filmGenresStorage;
//
//    @Autowired
//    public FilmGenresService(FilmGenresStorage filmGenresStorage) {
//        this.filmGenresStorage = filmGenresStorage;
//    }
//
//    public void addFilmGenres(Long filmId, Set<Genre> genres) {
//
//        for (Genre genre : genres) {
//
//            Long genreId = genre.getId();
//
//            filmGenresStorage.addFilmGenre(filmId, genreId);
//        }
//    }
//
//    public void deleteAllFilmGenresByFilmId(Long filmId) {
//        filmGenresStorage.deleteFilmGenresByFilmId(filmId);
//    }
//
//    public void updateFilmGenres(Long filmId, Set<Genre> genres) {
//        filmGenresStorage.deleteFilmGenresByFilmId(filmId);
//        addFilmGenres(filmId, genres);
//    }
//
//    public Set<Genre> getGenresByFilmId(Long filmId) {
//        return new HashSet<>(filmGenresStorage.getGenresByFilmId(filmId));
//    }
//}