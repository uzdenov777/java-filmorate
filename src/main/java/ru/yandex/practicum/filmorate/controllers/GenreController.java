package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.GenreService;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
 private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }
}
