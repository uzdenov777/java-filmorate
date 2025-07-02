package ru.yandex.practicum.filmorate.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Log4j2
@Service
public class FilmService {
    private final FilmStorage filmsStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage storage, InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
        this.filmsStorage = storage;
    }

    public Film add(Film film) {
        return filmsStorage.add(film);
    }

    public Film update(Film film) throws ResponseStatusException {
        return filmsStorage.update(film);
    }

    public List<Film> getAllFilms() {
        return filmsStorage.getAllFilms();
    }

    public void addLikeFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        Film filmLike = filmsStorage.getFilmById(filmId);
        Set<Long> likedUserIds = filmLike.getLikesFromUsers();

        likedUserIds.add(userId);
    }

    public void removeLikeFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        Film filmLike = filmsStorage.getFilmById(filmId);
        Set<Long> likedUserIds = filmLike.getLikesFromUsers();

        likedUserIds.remove(userId);
    }

    public List<Film> getListTopPopularFilms(int count) {
        List<Film> listTopPopularFilms = new ArrayList<>();
        List<Film> allFilms = getSortListFilms();

        for (int i = 0; i < count && i < allFilms.size(); i++) {
            listTopPopularFilms.add(allFilms.get(i));
        }

        return listTopPopularFilms;
    }

    private List<Film> getSortListFilms() throws ResponseStatusException {
        List<Film> allFilms = new ArrayList<>(filmsStorage.getAllFilms()); //копию для того, чтобы могли делать, что хотим с этим списком
        if (allFilms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не добавлено еще ни одного фильма для получения TOP-а");
        }

        allFilms.sort(Comparator.comparingInt((Film film) -> film.getLikesFromUsers().size()).reversed());
        return allFilms;
    }

    private void checkExistFilmAndUser(long filmId, long userId) throws ResponseStatusException {
        Film filmLike = filmsStorage.getFilmById(filmId);
        User userOne = userStorage.getUserById(userId);

        if (Objects.isNull(userOne)) {
            log.error("Пользователь с ID: {} не найден для добавления лайка фильму", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID: " + userId + " не найден для добавления лайка фильму");
        } else if (Objects.isNull(filmLike)) {
            log.error("Фильм с ID: {} не найден для добавления ему лайка", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID: " + filmId + " не найден для добавления ему лайка");
        }
    }
}
