package ru.yandex.practicum.filmorate.model.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class FilmResponse {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Long duration;

    private Mpa mpa;

    private List<Genre> genres = new ArrayList<>();
}
