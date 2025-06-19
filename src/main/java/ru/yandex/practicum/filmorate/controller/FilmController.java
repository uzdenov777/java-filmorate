package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private int newIdFilm;

    private final HashMap<Integer, Film> films = new HashMap<>();

    private int getNewId() { //Генерирует уникальный ID.
        newIdFilm++;
        return newIdFilm;
    }



    private static void isValidReleaseDate(Film film) throws ResponseStatusException {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate releaseDate = film.getReleaseDate();

        boolean isBefore = releaseDate.isBefore(minReleaseDate);
        boolean isEqual = releaseDate.isEqual(minReleaseDate);
        if ((isBefore || isEqual)) {
            log.error("Not Valid release date film :{}", film);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Valid release date film :" + film);
        }
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("Adding film {}", film);

        isValidReleaseDate(film); //В случае не валидного релиза вернется исключение ResponseStatusException
        film.setId(getNewId());

        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        int id = film.getId();
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

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Getting all films");
        return new ArrayList<>(films.values());
    }
}