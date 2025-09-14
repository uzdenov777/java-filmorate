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

    public void addLikeFilm(long filmId, long userId) {
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

    public void deleteLikesByFilmId(long filmId) {
        filmLikesDbStorage.deleteLikesByFilmId(filmId);
    }

    public void updateFilmLike(long filmId, Set<Long> userIds) {
        deleteLikesByFilmId(filmId);

        for (Long userId : userIds) {
            filmLikesDbStorage.addLikeFilm(filmId, userId);
        }
    }

    public Set<Long> getLikesByFilmId(long filmId) {
        return new HashSet<>(filmLikesDbStorage.getLikersIdsByFilmId(filmId));
    }
}