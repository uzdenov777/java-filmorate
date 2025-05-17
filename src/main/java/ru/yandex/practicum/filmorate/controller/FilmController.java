package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.yandex.practicum.filmorate.model.Film.isValidReleaseDate;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    HashMap<Integer, Film> films = new HashMap<>();
    int newIdFilm;

    public int getNewId() { //Генерирует уникальный ID.
        newIdFilm++;
        return newIdFilm;
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