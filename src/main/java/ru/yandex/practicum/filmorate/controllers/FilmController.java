package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
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
    public FilmDto add(@RequestBody @Valid FilmDto newFilm) {
        log.info("Adding film: {}", newFilm);

        FilmDto save = filmService.add(newFilm);
        return save;
    }

    @PutMapping
    public FilmDto update(@RequestBody @Valid FilmDto filmToUpdate) {
        log.info("Updating film: {}", filmToUpdate);

        FilmDto save = filmService.update(filmToUpdate);
        return save;
    }

    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable Long id) {
        log.info("Getting film with id: {}", id);

        FilmDto filmResponse = filmService.getFilmById(id);
        return filmResponse;
    }

    @GetMapping
    public List<FilmDto> getAllFilms(Pageable pageable) {
        log.info("Getting all films");
        return filmService.getAllFilms(pageable);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Liking film ID: {}, User ID: {}", id, userId);
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Removing like film ID:{}, User ID{}", id, userId);
        filmService.deleteLikeToFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Getting popular films top: {}", count);
        return filmService.getTopPopularFilms(count);
    }
}