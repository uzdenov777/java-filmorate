package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenresService genresService;

    public GenreController(GenresService genresService) {
        this.genresService = genresService;
    }

    @GetMapping
    public List<GenreDto> getAllGenres(Pageable pageable) {
        log.info("getAllGenres");
        return genresService.getAllGenres(pageable);
    }

    @GetMapping("/{id}")
    public GenreDto getGenreById(@PathVariable Long id) {
        log.info("getGenreById");
        return genresService.getGenreById(id);
    }
}