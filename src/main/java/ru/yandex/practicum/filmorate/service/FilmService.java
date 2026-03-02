package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.repository.FilmsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Log4j2
@Service
public class FilmService {

    private final FilmsRepository filmsRepository;

    private final UserService userService;
    private final FilmLikesService filmLikesService;
    private final GenresService genresService;
    private final MpaService mpaService;

    private final FilmMapper filmMapper;

    @Autowired
    public FilmService(FilmsRepository filmsRepository, UserService userService, FilmLikesService filmLikesService,
                       GenresService genresService, MpaService mpaService, FilmMapper filmMapper) {

        this.userService = userService;
        this.filmsRepository = filmsRepository;
        this.filmLikesService = filmLikesService;
        this.genresService = genresService;
        this.mpaService = mpaService;
        this.filmMapper = filmMapper;
    }

    @Transactional
    public FilmDto add(FilmDto newFilmDto) throws ResponseStatusException {

        validateFilm(newFilmDto);

        Film newFilm = filmMapper.toEntity(newFilmDto);
        Film saved = filmsRepository.save(newFilm);

        FilmDto filmResponse = filmMapper.toDto(saved);
        return filmResponse;
    }


    public FilmDto update(FilmDto filmDtoToUpdate) throws ResponseStatusException {

        validateFilm(filmDtoToUpdate);

        Long filmId = filmDtoToUpdate.getId();
        boolean filmExists = filmsRepository.existsById(filmId);

        if (!filmExists) {
            log.info("Не найден фильм для обновления с ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для обновления с ID: " + filmId);
        }

        Film filmToUpdate = filmMapper.toEntity(filmDtoToUpdate);
        Film saved = filmsRepository.save(filmToUpdate);

        FilmDto filmResponse = filmMapper.toDto(saved);
        return filmResponse;
    }

    public FilmDto getFilmById(Long filmId) throws ResponseStatusException {

        return filmsRepository.findById(filmId)
                .map(filmMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Не найден фильм для возвращения по ID: " + filmId));
    }

    public List<FilmDto> getAllFilms(Pageable pageable) {

        Page<Film> allFilms = filmsRepository.findAll(pageable);

        return filmMapper.toDtos(allFilms);
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

    public List<FilmDto> getTopPopularFilms(int count) {

        List<Film> listTopPopularFilms = filmLikesService.getTopPopularFilms(count);

        List<FilmDto> responses = filmMapper.toDtos(listTopPopularFilms);
        return responses;
    }

    private void checkExistFilmAndUser(long filmId, long userId) throws ResponseStatusException {

        boolean existFilm = filmsRepository.existsById(filmId);
        boolean existUser = userService.isUserExists(userId);

        if (!existUser) {
            log.error("Пользователь с ID: {} не найден для добавления лайка фильму", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID: " + userId + " не найден для добавления лайка фильму");
        }

        if (!existFilm) {
            log.error("Фильм с ID: {} не найден для добавления ему лайка", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID: " + filmId + " не найден для добавления ему лайка");
        }
    }

    private void validateFilm(FilmDto film) throws ResponseStatusException {

        isValidReleaseDate(film); //В случае не валидной даты релиза выбросит исключение ResponseStatusException

        Mpa mpa = film.getMpa();
        if (Objects.nonNull(mpa)) {
            Long mpaId = mpa.getId();
            mpaService.existsMpa(mpaId);
        }

        Set<GenreDto> genres = film.getGenres();
        if (!genres.isEmpty()) {
            genresService.allGenresExistByIds(genres);
        }
    }

    private void isValidReleaseDate(FilmDto filmToValidate) throws ResponseStatusException {
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