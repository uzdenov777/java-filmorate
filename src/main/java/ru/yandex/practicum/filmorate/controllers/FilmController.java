package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.enums.SortingType;
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

        return filmService.add(newFilm);
    }

    @PutMapping
    public FilmDto update(@RequestBody @Valid FilmDto filmToUpdate) {
        log.info("Updating film: {}", filmToUpdate);

        return filmService.update(filmToUpdate);
    }

    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable Long filmId) {
        log.info("Удаление фильма: {}", filmId);

        filmService.deleteFilmById(filmId);
    }

    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable Long id) {
        log.info("Getting film with id: {}", id);

        return filmService.getFilmById(id);
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
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count,
                                         @RequestParam(defaultValue = "0") Long genreId,
                                         @RequestParam(defaultValue = "0") Long year) {
        log.info("Getting popular films top:{}, genre:{}, year:{}", count, genreId, year);
        return filmService.getPopularFilmsByGenreAndYear(count, genreId, year);
    }

    @GetMapping("/common")
    public List<FilmDto> getCommonLikedFilms(@RequestParam Long userId,
                                             @RequestParam Long friendId,
                                             Pageable pageable) {
        log.info("Вернуть общие фильмы для пользователей 1: {}, 2: {}", userId, friendId);

        return filmService.getCommonLikedFilms(userId, friendId, pageable);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getFilmByDirectorId(@PathVariable Long directorId,
                                             @RequestParam SortingType type) {
        log.info("Возвращение фильмов режиссера: {} отсортированных по {}", directorId, type.toString());

        return filmService.getFilmByDirectorId(directorId, type);
    }
//    GET /films/director/{directorId}?sortBy=[year,likes]
//
//    Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.
}