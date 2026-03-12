package ru.yandex.practicum.filmorate.film;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.director.DirectorService;
import ru.yandex.practicum.filmorate.event.EventService;
import ru.yandex.practicum.filmorate.film.model.Film;
import ru.yandex.practicum.filmorate.film.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.filmLike.FilmLikesService;
import ru.yandex.practicum.filmorate.genre.GenresService;
import ru.yandex.practicum.filmorate.genre.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.mpa.Mpa;
import ru.yandex.practicum.filmorate.mpa.MpaService;
import ru.yandex.practicum.filmorate.user.UserService;
import ru.yandex.practicum.filmorate.user.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ru.yandex.practicum.filmorate.event.enums.EventType.LIKE;
import static ru.yandex.practicum.filmorate.event.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.event.enums.Operation.REMOVE;
import static ru.yandex.practicum.filmorate.film.SortingType.fromString;

@Log4j2
@AllArgsConstructor
@Service
public class FilmService {

    private final FilmsRepository filmsRepository;

    private final UserService userService;
    private final FilmLikesService filmLikesService;
    private final GenresService genresService;
    private final MpaService mpaService;
    private final EventService eventService;
    private final DirectorService directorService;

    private final FilmMapper filmMapper;

    @Transactional
    public FilmDto add(FilmDto newFilmDto) throws ResponseStatusException {
        validateFilm(newFilmDto);

        var newFilm = filmMapper.toEntity(newFilmDto);
        var saved = filmsRepository.save(newFilm);

        return filmMapper.toDto(saved);
    }


    public FilmDto update(FilmDto filmDtoToUpdate) throws ResponseStatusException {
        validateFilm(filmDtoToUpdate);

        var filmId = filmDtoToUpdate.getId();
        boolean filmExists = filmsRepository.existsById(filmId);

        if (!filmExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден фильм для обновления с ID: " + filmId);
        }

        var filmToUpdate = filmMapper.toEntity(filmDtoToUpdate);
        var saved = filmsRepository.save(filmToUpdate);

        return filmMapper.toDto(saved);
    }

    public void deleteFilmById(Long filmId) {
        log.info("Удаление фильма: {}", filmId);

        var exists = filmsRepository.existsById(filmId);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден фильм для удаления с ID: " + filmId);
        }

        filmsRepository.deleteById(filmId);
    }

    public FilmDto getFilmById(Long filmId) throws ResponseStatusException {
        return filmsRepository.findById(filmId)
                .map(filmMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Не найден фильм для возвращения по ID: " + filmId));
    }

    public List<FilmDto> getAllFilms(Pageable pageable) {
        var allFilms = filmsRepository.findAll(pageable);

        return filmMapper.toDtos(allFilms);
    }

    public List<FilmDto> getCommonLikedFilms(Long userId, Long friendId, Pageable pageable) {
        var exists = userService.isUserExists(userId) && userService.isUserExists(friendId);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND
                    , "Не найден один или два пользователя 1: " + userId + ", 2: " + friendId + " при возвращении общих фильмов");
        }

        var films = filmsRepository.findCommonLikedFilms(userId, friendId, pageable);
        return filmMapper.toDtos(films);
    }

    public void addLikeToFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);

        var filmProxy = filmsRepository.getReferenceById(filmId);
        var userProxy = userService.getUserProxyById(userId);

        filmLikesService.addLikeFilm(filmProxy, userProxy);
        eventService.save(userProxy, filmId, LIKE, ADD);
    }

    public void deleteLikeToFilm(long filmId, long userId) throws ResponseStatusException {
        checkExistFilmAndUser(filmId, userId);
        var userProxy = userService.getUserProxyById(userId);

        filmLikesService.deleteLikeFilm(filmId, userId);
        eventService.save(userProxy, filmId, LIKE, REMOVE);
    }

    public List<FilmDto> getPopularFilmsByGenreAndYear(int count, Long genreId, Long year) {
        var listTopPopularFilms = filmsRepository.getTopPopularFilms(count, genreId, year);

        return filmMapper.toDtos(listTopPopularFilms);
    }

    public List<FilmDto> getFilmByDirector(Long directorId, String sortBy, Pageable pageable) {
        directorService.existById(directorId);

        Page<Film> films = Page.empty();

        switch (fromString(sortBy)) {
            case YEAR -> films = filmsRepository.findByDirectorsIdOrderByReleaseDate(directorId, pageable);
            case LIKES -> films = filmsRepository.findByDirectorsIdOrderByLikes(directorId, pageable);
        }

        return filmMapper.toDtos(films);
    }

    public boolean isFilmExists(Long filmId) {
        return filmsRepository.existsById(filmId);
    }

    public List<FilmDto> searchFilms(String query, String by) {
        List<String> params = List.of(by.split(","));

        var byIsTitle = params.contains("title");
        var byIsDirector = params.contains("director");

        var films = filmsRepository.searchFilms(query, byIsTitle, byIsDirector);
        return filmMapper.toDtos(films);
    }

    private void checkExistFilmAndUser(long filmId, long userId) throws ResponseStatusException {
        var existFilm = filmsRepository.existsById(filmId);
        var existUser = userService.isUserExists(userId);

        if (!existUser) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID: " + userId + " не найден для добавления лайка фильму");
        }

        if (!existFilm) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID: " + filmId + " не найден для добавления ему лайка");
        }
    }

    private void validateFilm(FilmDto film) throws ResponseStatusException {
        isValidReleaseDate(film); //В случае не валидной даты релиза выбросит исключение ResponseStatusException

        Mpa mpa = film.getMpa();
        if (Objects.nonNull(mpa)) {
            mpaService.existsMpa(mpa.getId());
        }

        Set<GenreDto> genres = film.getGenres();
        if (!genres.isEmpty()) {
            genresService.allGenresExistByIds(genres);
        }
    }

    private void isValidReleaseDate(FilmDto filmToValidate) throws ResponseStatusException {
        var minReleaseDate = LocalDate.of(1895, 12, 28);
        var releaseDateFilm = filmToValidate.getReleaseDate();

        var isBefore = releaseDateFilm.isBefore(minReleaseDate);
        var isEqual = releaseDateFilm.isEqual(minReleaseDate);
        if ((isBefore || isEqual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не правильная дата релиза фильма: " + filmToValidate);
        }
    }
}