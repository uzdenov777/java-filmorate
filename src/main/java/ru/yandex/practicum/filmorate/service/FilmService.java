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
        checkedFilm(newFilm); //если у фильма все поля и их составляющие в норме, то просто не выбросит исключение ResponseStatusException

        Film save = filmsDbStorage.add(newFilm); // пробуем добавить фильм

        Long filmId = save.getId();
        Set<Genre> filmGenres = save.getGenres();
        Set<Long> filmLikersIds = save.getLikerIds();

        filmGenresService.addFilmGenres(filmId, filmGenres);
        filmLikesService.addFilmLikes(filmId, filmLikersIds);

        return save;
    }


    public Film update(Film filmToUpdate) throws ResponseStatusException {
        Long filmId = filmToUpdate.getId();

        boolean filmExists = filmsDbStorage.isFilmExists(filmId);
        if (!filmExists) {
            log.info("Не найден фильм для обновления с ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для обновления с ID: " + filmId);
        }

        checkedFilm(filmToUpdate); //если у фильма все поля и их составляющие в норме, то просто не выбросит исключение ResponseStatusException

        Film save = filmsDbStorage.update(filmToUpdate);

        Set<Genre> filmGenres = save.getGenres();
        Set<Long> filmLikersIds = save.getLikerIds();

        if (filmGenres.isEmpty()) {
            filmGenresService.deleteAllFilmGenresByFilmId(filmId);
        } else {
            filmGenresService.updateFilmGenres(filmId, filmGenres);
        }

        if (filmLikersIds.isEmpty()) {
            filmLikesService.deleteLikesByFilmId(filmId);
        } else {
            filmLikesService.updateFilmLike(filmId, filmLikersIds);
        }

        return save;
    }

    public Film getFilmById(Long filmId) throws ResponseStatusException {
        Optional<Film> filmOpt = filmsDbStorage.findById(filmId);
        if (filmOpt.isEmpty()) {
            log.info("Не найден фильм для возвращения по ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для возвращения по ID: " + filmId);
        }

        Film film = filmOpt.get();

        Set<Genre> filmGenres = filmGenresService.getGenresByFilmId(filmId);
        film.setGenres(filmGenres);

        Set<Long> filmLikersIds = filmLikesService.getLikesByFilmId(filmId);
        film.setLikerIds(filmLikersIds);

        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> allFilms = filmsDbStorage.findAll();

        for (Film film : allFilms) {
            Long filmId = film.getId();

            Set<Genre> filmGenres = filmGenresService.getGenresByFilmId(filmId);
            film.setGenres(filmGenres);

            Set<Long> filmLikersIds = filmLikesService.getLikesByFilmId(filmId);
            film.setLikerIds(filmLikersIds);
        }

        return allFilms;
    }

    public void addLikeToFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        filmLikesService.addLikeFilm(filmId, userId);
    }

    public void removeLikeToFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        filmLikesService.removeLikeFilm(filmId, userId);
    }

    public Set<Long> getLikersIdsByFilmId(long filmId) {
        boolean filmExists = filmsDbStorage.isFilmExists(filmId);
        if (!filmExists) {
            log.info("Для возвращения списка id пользователей поставивших лайк фильму по ID: {} не найден", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Для возвращения списка id пользователей поставивших лайк фильму по ID: " + filmId + " не найден");
        }

        return filmLikesService.getLikesByFilmId(filmId);
    }

    public List<Film> getListTopPopularFilms(int count) {
        List<Film> listTopPopularFilms = new ArrayList<>();
        List<Film> allFilms = getFilmsSortedByPopularity();

        for (int i = 0; i < count && i < allFilms.size(); i++) {
            listTopPopularFilms.add(allFilms.get(i));
        }

        return listTopPopularFilms;
    }

    private List<Film> getFilmsSortedByPopularity() throws ResponseStatusException {
        List<Film> allFilms = new ArrayList<>(getAllFilms()); //копию для того, чтобы могли делать, что хотим с этим списком

        if (allFilms.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не добавлено еще ни одного фильма для получения TOP-а");
        }

        allFilms.sort(Comparator.comparingInt((Film film) -> film.getLikerIds().size()).reversed());
        return allFilms;
    }

    private void checkExistFilmAndUser(long filmId, long userId) throws ResponseStatusException {
        boolean existFilm = filmsDbStorage.isFilmExists(filmId);
        boolean existUser = userService.isUserExists(userId);

        if (!existUser) {
            log.error("Пользователь с ID: {} не найден для добавления лайка фильму", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID: " + userId + " не найден для добавления лайка фильму");
        } else if (!existFilm) {
            log.error("Фильм с ID: {} не найден для добавления ему лайка", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID: " + filmId + " не найден для добавления ему лайка");
        }
    }

    private void checkedFilm(Film chekFilm) throws ResponseStatusException {
        isValidReleaseDate(chekFilm); //В случае не валидной даты релиза выбросит исключение ResponseStatusException

        Mpa mpa = chekFilm.getMpa();
        if (Objects.nonNull(mpa)) {
            int mpaId = mpa.getId();
            mpaService.isExistsMpa(mpaId);
        }

        Set<Genre> filmGenres = chekFilm.getGenres();
        for (Genre genre : filmGenres) {
            int genreId = genre.getId();
            genresService.isGenreExist(genreId);
        }

        Set<Long> findLikersIds = chekFilm.getLikerIds();
        for (Long userId : findLikersIds) {
            userService.isUserExists(userId);
        }
    }

    private void isValidReleaseDate(Film filmToValidate) throws ResponseStatusException {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate releaseDateFilm = filmToValidate.getReleaseDate();

        boolean isBefore = releaseDateFilm.isBefore(minReleaseDate);
        boolean isEqual = releaseDateFilm.isEqual(minReleaseDate);
        if ((isBefore || isEqual)) {
            log.error("Не правильная дата релиза фильма: {}", filmToValidate);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не правильная дата релиза фильма: " + filmToValidate);
        }
    }
}