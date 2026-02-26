package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.FilmRequest;
import ru.yandex.practicum.filmorate.model.dto.FilmResponse;
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
    public FilmResponse add(@RequestBody @Valid FilmRequest newFilm) {
        log.info("Adding film: {}", newFilm);

        FilmResponse save = filmService.add(newFilm);

        return save;
    }

    @PutMapping
    public FilmResponse update(@Valid @RequestBody FilmRequest filmToUpdate) {
        log.info("Updating film: {}", filmToUpdate);

        FilmResponse save = filmService.update(filmToUpdate);
        return save;
    }

    @GetMapping("/{id}")
    public FilmResponse getById(@PathVariable Long id) {

        log.info("Getting film with id: {}", id);

        FilmResponse filmResponse = filmService.getFilmById(id);

        return filmResponse;
    }

//    @GetMapping
//    public List<Film> getAllFilms() {
//
//        log.info("Getting all films");
//        return filmService.getAllFilms();
//    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Liking film ID: {}, User ID: {}", id, userId);
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Removing like film ID:{}, User ID{}", id, userId);
        filmService.removeLikeToFilm(id, userId);
    }

//    @GetMapping("/popular")
//    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
//        log.info("Getting popular films top: {}", count);
//        return filmService.getListTopPopularFilms(count);
//    }
}