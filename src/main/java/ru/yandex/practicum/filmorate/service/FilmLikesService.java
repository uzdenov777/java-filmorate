package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmLikesRepository;

import java.util.List;

@Service
public class FilmLikesService {

    private final FilmLikesRepository filmLikesRepository;

    public FilmLikesService(FilmLikesRepository filmLikesRepository) {
        this.filmLikesRepository = filmLikesRepository;
    }

    public void addLikeFilm(Film film, User user) {

        FilmLike filmLike = new FilmLike();
        filmLike.setFilm(film);
        filmLike.setUser(user);

        filmLikesRepository.save(filmLike);
    }

    @Transactional
    public void deleteLikeFilm(long filmId, long userId) {
        filmLikesRepository.deleteLikeFilm(filmId, userId);
    }

    public List<Film> getTopPopularFilms(int count) {
        return filmLikesRepository.getTopPopularFilms(count);
    }
}