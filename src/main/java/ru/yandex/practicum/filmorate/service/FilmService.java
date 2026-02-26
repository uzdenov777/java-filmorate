package ru.yandex.practicum.filmorate.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.dto.FilmRequest;
import ru.yandex.practicum.filmorate.model.dto.FilmResponse;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmsStorage;

import java.time.LocalDate;
import java.util.*;

@Log4j2
@Service
public class FilmService {

    private final FilmsStorage filmsStorage;

    private final UserService userService;
    private final FilmLikesService filmLikesService;
//    private final FilmGenresService filmGenresService;
    private final GenresService genresService;
    private final MpaService mpaService;

    @Autowired
    public FilmService(FilmsStorage filmsStorage, UserService userService, FilmLikesService filmLikesService, GenresService genresService, MpaService mpaService) {
        this.userService = userService;
        this.filmsStorage = filmsStorage;
        this.filmLikesService = filmLikesService;
//        this.filmGenresService = filmGenresService;
        this.genresService = genresService;
        this.mpaService = mpaService;
    }

    public FilmResponse add(FilmRequest newFilmRequest) throws ResponseStatusException {

        Film newFilm = toFilm(newFilmRequest);

        checkedFilm(newFilm); //если у фильма все поля и их составляющие в норме, то просто не выбросит исключение ResponseStatusException

        Film saved = filmsStorage.save(newFilm);

        FilmResponse filmResponse = toFilmResponse(saved);

        return filmResponse;
    }


    public FilmResponse update(FilmRequest filmRequestToUpdate) throws ResponseStatusException {

        Long filmId = filmRequestToUpdate.getId();

        boolean filmExists = filmsStorage.existsById(filmId);
        if (!filmExists) {
            log.info("Не найден фильм для обновления с ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для обновления с ID: " + filmId);
        }

        Film filmToUpdate = toFilm(filmRequestToUpdate);

        checkedFilm(filmToUpdate); //если у фильма все поля и их составляющие в норме, то просто не выбросит исключение ResponseStatusException

        Film saved = filmsStorage.save(filmToUpdate);

        FilmResponse filmResponse = toFilmResponse(saved);

        return filmResponse;
    }

    public FilmResponse getFilmById(Long filmId) throws ResponseStatusException {

        Optional<Film> filmOpt = filmsStorage.findById(filmId);

        if (filmOpt.isEmpty()) {
            log.info("Не найден фильм для возвращения по ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для возвращения по ID: " + filmId);
        }

        Film film = filmOpt.get();

        FilmResponse filmResponse = toFilmResponse(film);

//        Set<Genre> filmGenres = filmGenresService.getGenresByFilmId(filmId);
//        filmResponse.setGenres(filmGenres);

        Set<Long> likedIds = filmLikesService.getLikesByFilmId(filmId);
        filmResponse.setLikerIds(likedIds);

        return filmResponse;
    }

//    public List<Film> getAllFilms() {
//
//        List<Film> allFilms = filmsStorage.findAll();
//
//        for (Film film : allFilms) {
//            Long filmId = film.getId();
//
//            Set<Genre> filmGenres = filmGenresService.getGenresByFilmId(filmId);
//            film.setGenres(filmGenres);
//
//            Set<Long> filmLikersIds = filmLikesService.getLikesByFilmId(filmId);
//            film.setLikerIds(filmLikersIds);
//        }
//
//        return allFilms;
//    }

    public void addLikeToFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        filmLikesService.addLikeFilm(filmId, userId);
    }

    public void removeLikeToFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        filmLikesService.removeLikeFilm(filmId, userId);
    }

//    public List<Film> getListTopPopularFilms(int count) {
//        List<Film> listTopPopularFilms = new ArrayList<>();
//        List<Film> allFilms = getFilmsSortedByPopularity();
//
//        for (int i = 0; i < count && i < allFilms.size(); i++) {
//            listTopPopularFilms.add(allFilms.get(i));
//        }
//
//        return listTopPopularFilms;
//    }

    private Film toFilm(FilmRequest newFilmRequest) {

        Long id = newFilmRequest.getId();
        String name = newFilmRequest.getName();
        String description = newFilmRequest.getDescription();
        LocalDate releaseDate = newFilmRequest.getReleaseDate();
        Long duration = newFilmRequest.getDuration();

        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        return film;
    }

    private FilmResponse toFilmResponse(Film film) {

        Long id = film.getId();
        String name = film.getName();
        String description = film.getDescription();
        LocalDate releaseDate = film.getReleaseDate();
        Long duration = film.getDuration();
        Mpa mpa = film.getMpa();

        FilmResponse filmResponse = new FilmResponse();
        filmResponse.setId(id);
        filmResponse.setName(name);
        filmResponse.setDescription(description);
        filmResponse.setReleaseDate(releaseDate);
        filmResponse.setDuration(duration);
        filmResponse.setMpa(mpa);

        return filmResponse;
    }

//    private List<Film> getFilmsSortedByPopularity() throws ResponseStatusException {
//        List<Film> allFilms = new ArrayList<>(getAllFilms()); //копию для того, чтобы могли делать, что хотим с этим списком
//
//        if (allFilms.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не добавлено еще ни одного фильма для получения TOP-а");
//        }
//
//        allFilms.sort(Comparator.comparingInt((Film film) -> film.getLikerIds().size()).reversed());
//        return allFilms;
//    }

    private void checkExistFilmAndUser(long filmId, long userId) throws ResponseStatusException {

        boolean existFilm = filmsStorage.existsById(filmId);
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