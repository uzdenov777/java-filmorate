package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.FilmRequest;
import ru.yandex.practicum.filmorate.model.dto.FilmResponse;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmsRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
public class FilmService {

    private final FilmsRepository filmsRepository;

    private final UserService userService;
    private final FilmLikesService filmLikesService;
    private final FilmGenresService filmGenresService;
    private final GenresService genresService;
    private final MpaService mpaService;

    @Autowired
    public FilmService(FilmsRepository filmsRepository, UserService userService, FilmLikesService filmLikesService,
                       FilmGenresService filmGenresService, GenresService genresService, MpaService mpaService) {

        this.userService = userService;
        this.filmsRepository = filmsRepository;
        this.filmLikesService = filmLikesService;
        this.filmGenresService = filmGenresService;
        this.genresService = genresService;
        this.mpaService = mpaService;
    }

    @Transactional
    public FilmResponse add(FilmRequest newFilmRequest) throws ResponseStatusException {

        isValidReleaseDate(newFilmRequest);

        Film newFilm = toFilm(newFilmRequest);
        Film saved = filmsRepository.save(newFilm);

        List<Genre> genres = newFilmRequest.getGenres();
        filmGenresService.addFilmGenres(saved, genres);

        List<Genre> fullGenres = genresService.getGenres(genres);

        FilmResponse filmResponse = toFilmResponse(saved, fullGenres);

        return filmResponse;
    }


    public FilmResponse update(FilmRequest filmRequestToUpdate) throws ResponseStatusException {

        Long filmId = filmRequestToUpdate.getId();

        boolean filmExists = filmsRepository.existsById(filmId);
        if (!filmExists) {
            log.info("Не найден фильм для обновления с ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для обновления с ID: " + filmId);
        }

        isValidReleaseDate(filmRequestToUpdate);

        Film filmToUpdate = toFilm(filmRequestToUpdate);
        Film saved = filmsRepository.save(filmToUpdate);

        List<Genre> genres = filmRequestToUpdate.getGenres();
        filmGenresService.updateFilmGenres(saved, genres);

        List<Genre> fullGenres = genresService.getGenres(genres);
        FilmResponse filmResponse = toFilmResponse(saved, fullGenres);

        return filmResponse;
    }

    public FilmResponse getFilmById(Long filmId) throws ResponseStatusException {

        Optional<Film> filmOpt = filmsRepository.findById(filmId);

        if (filmOpt.isEmpty()) {
            log.info("Не найден фильм для возвращения по ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для возвращения по ID: " + filmId);
        }

        Film film = filmOpt.get();
        List<Genre> genres = filmGenresService.getGenresByFilm(film);

        FilmResponse filmResponse = toFilmResponse(film, genres);

        return filmResponse;
    }

    public List<FilmResponse> getAllFilmsResponse() {

        List<Film> allFilms = getAllFilms();

        List<FilmResponse> responses = new ArrayList<>();
        for (Film film : allFilms) {

            List<Genre> genres = filmGenresService.getGenresByFilm(film);
            FilmResponse filmResponse = toFilmResponse(film, genres);

            responses.add(filmResponse);
        }

        return responses;
    }

    public void addLikeToFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        Film filmProxy = filmsRepository.getReferenceById(filmId);
        User secondUserProxy = userService.getUserProxyById(userId);

        filmLikesService.addLikeFilm(filmProxy, secondUserProxy);
    }

    public void deleteLikeToFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        filmLikesService.deleteLikeFilm(filmId, userId);
    }

    public List<Film> getTopPopularFilms(int count) {

        List<Film> listTopPopularFilms = filmLikesService.getTopPopularFilms(count);

        return listTopPopularFilms;
    }

    private Film toFilm(FilmRequest newFilmRequest) {

        Long id = newFilmRequest.getId();
        String name = newFilmRequest.getName();
        String description = newFilmRequest.getDescription();
        LocalDate releaseDate = newFilmRequest.getReleaseDate();
        Long duration = newFilmRequest.getDuration();
        Long mpaId = newFilmRequest.getMpa().getId();
        //полностью подтягиваем MPA
        Mpa mpa = mpaService.getMpaById(mpaId);

        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpa(mpa);

        return film;
    }


    private FilmResponse toFilmResponse(Film film, List<Genre> genres) {

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
        filmResponse.setGenres(genres);

        return filmResponse;
    }

    private List<Film> getAllFilms() {

        List<Film> allFilms = filmsRepository.findAll();

        return allFilms;
    }

    private void checkExistFilmAndUser(long filmId, long userId) throws ResponseStatusException {

        boolean existFilm = filmsRepository.existsById(filmId);

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

//        isValidReleaseDate(chekFilm); //В случае не валидной даты релиза выбросит исключение ResponseStatusException

//        Mpa mpa = chekFilm.getMpa();
//        if (Objects.nonNull(mpa)) {
//            int mpaId = mpa.getId();
//            mpaService.isExistsMpa(mpaId);
//        }
    }

    private void isValidReleaseDate(FilmRequest filmToValidate) throws ResponseStatusException {
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