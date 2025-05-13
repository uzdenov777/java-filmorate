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
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        isValidReleaseDate(film);//В случае не валидного релиза вернется исключение ResponseStatusException

        boolean isExistingFilm = films.containsKey(film.getId());
        if (isExistingFilm) {
            log.error("Already exists film with id {}", film.getId());
            throw new ResponseStatusException(HttpStatus.FOUND, "Film ID:" + film.getId() + " Exists");
        }

        log.info("Adding film {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        int id = film.getId();
        isValidReleaseDate(film);
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

    private void isValidReleaseDate(Film film) throws ResponseStatusException {
        int id = film.getId();
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate releaseDate = film.getReleaseDate();
        if (Objects.isNull(releaseDate)) {
            log.error("Film ID:{} null release date", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film ID:" + id + " null release date");
        }

        boolean isAfter = releaseDate.isAfter(minReleaseDate);
        boolean isEqual = releaseDate.isEqual(minReleaseDate);
        if (!(isAfter || isEqual)) {
            log.error("Film ID:{} Not Valid release date", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Film ID:" + id + " Not Valid release date");
        }
    }
}
