package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;

public interface FilmLikesStorage {

    void addLikeFilm(long filmId, long userId);

    void removeLikeFilm(long filmId, long userId);

    void deleteLikesByFilmId(long filmId);

    List<Long> getLikersIdsByFilmId(long filmId);
}