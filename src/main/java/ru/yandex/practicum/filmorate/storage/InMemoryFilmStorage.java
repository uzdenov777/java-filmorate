package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.yandex.practicum.filmorate.model.Film.isValidReleaseDate;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    HashMap<Long, Film> films = new HashMap<>();
    long newIdFilm;

    @Override
    public long getNewId() { //Генерирует уникальный ID.
        newIdFilm++;
        return newIdFilm;
    }

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

    @Override
    public Film getFilmById(long id) {
        return films.get(id);
    }
}
