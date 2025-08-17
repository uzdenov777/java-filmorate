package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@RequestBody @Valid Film newFilm) {
        log.info("Adding film: {}", newFilm);
        return filmService.add(newFilm);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film filmToUpdate) {
        log.info("Updating film: {}", filmToUpdate);
        return filmService.update(filmToUpdate);
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        log.info("Getting film with id: {}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Getting all films");
        return filmService.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable int id, @PathVariable int userId) {
        log.info("Liking film");
        filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Removing like film");
        filmService.removeLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Getting popular films");
        return filmService.getListTopPopularFilms(count);
    }
}