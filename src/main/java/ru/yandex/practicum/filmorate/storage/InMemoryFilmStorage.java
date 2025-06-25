package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
   private final HashMap<Long, Film> films = new HashMap<>();
    private long newIdFilm;

    @Override
    public Film add(Film film) {
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
    public List<Film> getAllFilms() {
        log.info("Getting all films");
        return new ArrayList<>(films.values());
    }

    private long getNewId() { //Генерирует уникальный ID.
        newIdFilm++;
        return newIdFilm;
    }

    @Override
    public Film getFilmById(long id) {
        return films.get(id);
    }

    private void isValidReleaseDate(Film film) throws ResponseStatusException {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate releaseDate = film.getReleaseDate();

        boolean isBefore = releaseDate.isBefore(minReleaseDate);
        boolean isEqual = releaseDate.isEqual(minReleaseDate);
        if ((isBefore || isEqual)) {
            log.error("Not Valid release date film :{}", film);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Valid release date film :" + film);
        }
    }
}