package ru.yandex.practicum.filmorate.film.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.director.model.DirectorDto;
import ru.yandex.practicum.filmorate.genre.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.mpa.Mpa;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {

    @Positive(message = "ID у фильма не должна быть отрицательным")
    private Long id;

    @NotBlank(message = "Название у фильмы не может отсутствовать")
    private String name;

    @NotBlank(message = "Описание у фильма не должно отсутствовать")
    @Size(max = 200, message = "Описание у фильма не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза у фильмы не может отсутствовать")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность у фильмы не может отсутствовать")
    @Positive(message = "Продолжительность у фильма не должна быть отрицательной")
    private Long duration;

    @NotNull(message = "Ограничение у фильмы не может отсутствовать")
    private Mpa mpa;

    private Set<GenreDto> genres = new HashSet<>();

    private Set<DirectorDto> directors = new HashSet<>();
}