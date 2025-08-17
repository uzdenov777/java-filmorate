package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDbStorage;

import java.util.HashSet;
import java.util.Set;

@Service
public class FilmLikesService {
    private final FilmLikesDbStorage filmLikesDbStorage;


    public FilmLikesService(FilmLikesDbStorage filmLikesDbStorage) {
        this.filmLikesDbStorage = filmLikesDbStorage;
    }

    public void addFilmLike(long filmId, long userId) {
        filmLikesDbStorage.addLikeFilm(filmId, userId);
    }

    public void addFilmLikes(long filmId, Set<Long> userIds) {
        for (Long userId : userIds) {
            filmLikesDbStorage.addLikeFilm(filmId, userId);
        }
    }

    public void removeLikeFilm(long filmId, long userId) {
        filmLikesDbStorage.removeLikeFilm(filmId, userId);
    }

    public void deleteAllFilmLikesByFilmId(long filmId) {
        filmLikesDbStorage.deleteAllFilmLikesByFilmId(filmId);
    }

    public void updateFilmLike(long filmId, Set<Long> userIds) {
        deleteAllFilmLikesByFilmId(filmId);

        for (Long userId : userIds) {
            filmLikesDbStorage.addLikeFilm(filmId, userId);
        }
    }

    public Set<Long> getFilmLikesByFilmId(long filmId) {
        return new HashSet<>(filmLikesDbStorage.getFilmLikesByFilmId(filmId));
    }
}