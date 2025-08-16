package ru.yandex.practicum.filmorate.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
public class FilmService {
    private final FilmDbStorage filmDbStorage;

    private final UserService userService;
    private final FilmLikesService filmLikesService;
    private final FilmGenresService filmGenresService;
    private final GenreService genreService;
    private final MpaService mpaService;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, UserService userService, FilmLikesService filmLikesService, FilmGenresService filmGenresService, GenreService genreService, MpaService mpaService) {
        this.userService = userService;
        this.filmDbStorage = filmDbStorage;
        this.filmLikesService = filmLikesService;
        this.filmGenresService = filmGenresService;
        this.genreService = genreService;
        this.mpaService = mpaService;
    }

    public Film add(Film film) throws ResponseStatusException {
        isValidFilm(film); //если у фильма все понял и их составляющие в норме, то просто не выбросит исключение ResponseStatusException

        filmDbStorage.add(film); // пробуем добавить фильм

        Long filmId = film.getId();
        Set<Genre> genres = film.getGenres();
        Set<Long> likesFromUsersId = film.getLikesFromUsers();

        filmGenresService.addFilmGenres(filmId, genres);
        filmLikesService.addFilmLikes(filmId, likesFromUsersId);

        return film;
    }


    public Film update(Film film) throws ResponseStatusException {
        Long filmId = film.getId();

        boolean filmExists = filmDbStorage.isFilmExists(filmId);
        if (!filmExists) {
            log.info("Не найден фильм для обновления с ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для обновления с ID: " + filmId);
        }

        isValidFilm(film); //если у фильма все поля и их составляющие в норме, то просто не выбросит исключение ResponseStatusException

        filmDbStorage.update(film);

        Set<Genre> genres = film.getGenres();
        Set<Long> likesFromUsers = film.getLikesFromUsers();

        if (genres.isEmpty()) {
            filmGenresService.deleteAllFilmGenresByFilmId(filmId);
        } else {
            filmGenresService.updateFilmGenres(filmId, genres);
        }

        if (likesFromUsers.isEmpty()) {
            filmLikesService.deleteAllFilmLikesByFilmId(filmId);
        } else {
            filmLikesService.updateFilmLike(filmId, likesFromUsers);
        }

        return film;
    }

    public Film getFilmById(Long filmId) throws ResponseStatusException {
        boolean filmExists = filmDbStorage.isFilmExists(filmId);
        if (!filmExists) {
            log.info("Не найден фильм для возвращения по ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для возвращения по ID: " + filmId);
        }

        Film film = filmDbStorage.getFilmById(filmId);
        film.setGenres(genreService.getGenresByFilmId(filmId));
        film.setLikesFromUsers(filmLikesService.getFilmLikesByFilmId(filmId));

        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> allFilms = filmDbStorage.getAllFilms();

        for (Film film : allFilms) {
            Long filmId = film.getId();
            film.setGenres(genreService.getGenresByFilmId(filmId));
            film.setLikesFromUsers(filmLikesService.getFilmLikesByFilmId(filmId));
        }

        return allFilms;
    }

    public void addLikeFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        filmLikesService.addFilmLike(filmId, userId);
    }

    public void removeLikeFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        filmLikesService.removeLikeFilm(filmId, userId);
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

        allFilms.sort(Comparator.comparingInt((Film film) -> film.getLikesFromUsers().size()).reversed());
        return allFilms;
    }

    private void checkExistFilmAndUser(long filmId, long userId) throws ResponseStatusException {
        boolean existFilmLike = filmDbStorage.isFilmExists(filmId);
        boolean existUserFirst = userService.isUserExists(userId);


        if (!existUserFirst) {
            log.error("Пользователь с ID: {} не найден для добавления лайка фильму", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID: " + userId + " не найден для добавления лайка фильму");
        } else if (!existFilmLike) {
            log.error("Фильм с ID: {} не найден для добавления ему лайка", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID: " + filmId + " не найден для добавления ему лайка");
        }
    }

    private void isValidFilm(Film film) throws ResponseStatusException {
        isValidReleaseDate(film); //В случае не валидного релиза выбросит исключение ResponseStatusException
        mpaService.isExistsMpa(film.getMpa().getId());

        Set<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            genreService.isGenreExist(genre.getId());
        }

        Set<Long> likesFromUsers = film.getLikesFromUsers();
        for (Long userId : likesFromUsers) {
            userService.isUserExists(userId);
        }
    }

    private void isValidReleaseDate(Film film) throws ResponseStatusException {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate releaseDateFilm = film.getReleaseDate();

        boolean isBefore = releaseDateFilm.isBefore(minReleaseDate);
        boolean isEqual = releaseDateFilm.isEqual(minReleaseDate);
        if ((isBefore || isEqual)) {
            log.error("Not Valid release date film :{}", film);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Valid release date film :" + film);
        }
    }
}