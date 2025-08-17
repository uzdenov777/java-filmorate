package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmsStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Slf4j
@Component
public class InMemoryFilmStorage implements FilmsStorage {
    private final HashMap<Long, Film> films = new HashMap<>();
    private static long newIdFilm;

    @Override
    public Film add(Film film) throws ResponseStatusException {
        log.info("Adding film {}", film);

        isValidReleaseDate(film); //В случае не валидного релиза вернется исключение ResponseStatusException
        film.setId(getNewId());

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) throws ResponseStatusException {
        long id = film.getId();
        isValidReleaseDate(film); //В случае не валидного релиза вернется исключение ResponseStatusException
        boolean isExistingFilm = films.containsKey(id);

        if (!isExistingFilm) {
            log.error("Not exists film with  ID:{}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film ID:" + id + " Not Found");
        }

        log.info("Updating film {}", film);
        films.put(id, film);
        return film;
    }

    @Override
    public Film getFilmById(Long filmId) {
        return null;
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Getting all films");
        return new ArrayList<>(films.values());
    }

    private long getNewId() { //Генерирует уникальный ID.
        newIdFilm++;
        return newIdFilm;
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