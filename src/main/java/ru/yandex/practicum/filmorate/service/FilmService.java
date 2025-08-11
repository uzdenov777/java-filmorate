package ru.yandex.practicum.filmorate.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Log4j2
@Service
public class FilmService {
    private final FilmDbStorage filmDbStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, UserService userService) {
        this.userService = userService;
        this.filmDbStorage = filmDbStorage;
    }

    public Film add(Film film) {
        return filmDbStorage.add(film);
    }

    public Film update(Film film) throws ResponseStatusException {
        return filmDbStorage.update(film);
    }

    public List<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    public void addLikeFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        Film filmLike = filmDbStorage.getFilmById(filmId);
        Set<Long> likedUserIds = filmLike.getLikesFromUsers();

        likedUserIds.add(userId);
    }

    public void removeLikeFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        Film filmLike = filmDbStorage.getFilmById(filmId);
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
        List<Film> allFilms = new ArrayList<>(filmDbStorage.getAllFilms()); //копию для того, чтобы могли делать, что хотим с этим списком
        if (allFilms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не добавлено еще ни одного фильма для получения TOP-а");
        }

        allFilms.sort(Comparator.comparingInt((Film film) -> film.getLikesFromUsers().size()).reversed());
        return allFilms;
    }

    private void checkExistFilmAndUser(long filmId, long userId) throws ResponseStatusException {
        Optional<Film> filmLike = Optional.ofNullable(filmDbStorage.getFilmById(filmId));
        Optional<User> userFirst = Optional.ofNullable(userService.getUserById(userId));



        if (userFirst.isEmpty()) {
            log.error("Пользователь с ID: {} не найден для добавления лайка фильму", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID: " + userId + " не найден для добавления лайка фильму");
        } else if (filmLike.isEmpty()) {
            log.error("Фильм с ID: {} не найден для добавления ему лайка", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID: " + filmId + " не найден для добавления ему лайка");
        }
    }
}