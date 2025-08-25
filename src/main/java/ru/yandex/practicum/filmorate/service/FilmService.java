package ru.yandex.practicum.filmorate.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmsDbStorage;

import java.time.LocalDate;
import java.util.*;

@Log4j2
@Service
public class FilmService {
    private final FilmsDbStorage filmsDbStorage;

    private final UserService userService;
    private final FilmLikesService filmLikesService;
    private final FilmGenresService filmGenresService;
    private final GenresService genresService;
    private final MpaService mpaService;

    @Autowired
    public FilmService(FilmsDbStorage filmsDbStorage, UserService userService, FilmLikesService filmLikesService, FilmGenresService filmGenresService, GenresService genresService, MpaService mpaService) {
        this.userService = userService;
        this.filmsDbStorage = filmsDbStorage;
        this.filmLikesService = filmLikesService;
        this.filmGenresService = filmGenresService;
        this.genresService = genresService;
        this.mpaService = mpaService;
    }

    public Film add(Film newFilm) throws ResponseStatusException {
        isValidFilm(newFilm); //если у фильма все понял и их составляющие в норме, то просто не выбросит исключение ResponseStatusException

        filmsDbStorage.add(newFilm); // пробуем добавить фильм

        Long newFilmId = newFilm.getId();
        Set<Genre> genresOfNewFilm = newFilm.getGenres();
        Set<Long> likesUsersIdOfNewFilm = newFilm.getLikesUsersId();

        filmGenresService.addFilmGenres(newFilmId, genresOfNewFilm);
        filmLikesService.addFilmLikes(newFilmId, likesUsersIdOfNewFilm);

        return newFilm;
    }


    public Film update(Film filmToUpdate) throws ResponseStatusException {
        Long filmId = filmToUpdate.getId();

        boolean filmExists = filmsDbStorage.isFilmExists(filmId);
        if (!filmExists) {
            log.info("Не найден фильм для обновления с ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для обновления с ID: " + filmId);
        }

        isValidFilm(filmToUpdate); //если у фильма все поля и их составляющие в норме, то просто не выбросит исключение ResponseStatusException

        filmsDbStorage.update(filmToUpdate);

        Set<Genre> genres = filmToUpdate.getGenres();
        Set<Long> likesUsersId = filmToUpdate.getLikesUsersId();

        if (genres.isEmpty()) {
            filmGenresService.deleteAllFilmGenresByFilmId(filmId);
        } else {
            filmGenresService.updateFilmGenres(filmId, genres);
        }

        if (likesUsersId.isEmpty()) {
            filmLikesService.deleteAllFilmLikesByFilmId(filmId);
        } else {
            filmLikesService.updateFilmLike(filmId, likesUsersId);
        }

        return filmToUpdate;
    }

    public Film getFilmById(Long filmId) throws ResponseStatusException {
        boolean filmExists = filmsDbStorage.isFilmExists(filmId);
        if (!filmExists) {
            log.info("Не найден фильм для возвращения по ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для возвращения по ID: " + filmId);
        }

        Film film = filmsDbStorage.getFilmById(filmId);
        film.setGenres(filmGenresService.getGenresByFilmId(filmId));
        film.setLikesUsersId(filmLikesService.getFilmLikesByFilmId(filmId));

        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> allFilms = filmsDbStorage.getAllFilms();

        for (Film film : allFilms) {
            Long filmId = film.getId();
            film.setGenres(filmGenresService.getGenresByFilmId(filmId));
            film.setLikesUsersId(filmLikesService.getFilmLikesByFilmId(filmId));
        }

        return allFilms;
    }

    public void addLikeFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        filmLikesService.addLikeFilm(filmId, userId);
    }

    public void removeLikeFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        filmLikesService.removeLikeFilm(filmId, userId);
    }

    public Set<Long> getLikesUsersIdByFilmId(long filmId) {
        boolean filmExists = filmsDbStorage.isFilmExists(filmId);
        if (!filmExists) {
            log.info("Для возвращения списка id пользователей поставивших лайк фильму по ID: {} не найден", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Для возвращения списка id пользователей поставивших лайк фильму по ID: " + filmId + " не найден");
        }

        return filmLikesService.getFilmLikesByFilmId(filmId);
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
        List<Film> allFilms = new ArrayList<>(getAllFilms()); //копию для того, чтобы могли делать, что хотим с этим списком
        if (allFilms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не добавлено еще ни одного фильма для получения TOP-а");
        }

        allFilms.sort(Comparator.comparingInt((Film film) -> film.getLikesUsersId().size()).reversed());
        return allFilms;
    }

    private void checkExistFilmAndUser(long filmId, long userId) throws ResponseStatusException {
        boolean existFilmLike = filmsDbStorage.isFilmExists(filmId);
        boolean existUserFirst = userService.isUserExists(userId);


        if (!existUserFirst) {
            log.error("Пользователь с ID: {} не найден для добавления лайка фильму", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID: " + userId + " не найден для добавления лайка фильму");
        } else if (!existFilmLike) {
            log.error("Фильм с ID: {} не найден для добавления ему лайка", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID: " + filmId + " не найден для добавления ему лайка");
        }
    }

    private void isValidFilm(Film chekFilm) throws ResponseStatusException {
        isValidReleaseDate(chekFilm); //В случае не валидного релиза выбросит исключение ResponseStatusException

        Mpa mpa = chekFilm.getMpa();
        if (Objects.nonNull(mpa)) {
            mpaService.isExistsMpa(chekFilm.getMpa().getId());
        }

        Set<Genre> genres = chekFilm.getGenres();
        for (Genre genre : genres) {
            genresService.isGenreExist(genre.getId());
        }

        Set<Long> likesUsers = chekFilm.getLikesUsersId();
        for (Long userId : likesUsers) {
            userService.isUserExists(userId);
        }
    }

    private void isValidReleaseDate(Film filmToValidate) throws ResponseStatusException {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate releaseDateFilm = filmToValidate.getReleaseDate();

        boolean isBefore = releaseDateFilm.isBefore(minReleaseDate);
        boolean isEqual = releaseDateFilm.isEqual(minReleaseDate);
        if ((isBefore || isEqual)) {
            log.error("Not Valid release date film :{}", filmToValidate);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Valid release date film :" + filmToValidate);
        }
    }
}