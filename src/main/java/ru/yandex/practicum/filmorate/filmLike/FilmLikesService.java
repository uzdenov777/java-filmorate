package ru.yandex.practicum.filmorate.filmLike;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.film.model.Film;
import ru.yandex.practicum.filmorate.user.model.User;


@AllArgsConstructor
@Service
public class FilmLikesService {

    private final FilmLikesRepository filmLikesRepository;

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
}