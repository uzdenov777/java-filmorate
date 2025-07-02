package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 * Название не может быть пустым;
 * максимальная длина описания — 200 символов;
 * дата релиза — не раньше 28 декабря 1895 года;
 * продолжительность фильма должна быть положительной.
 */
@Slf4j
@Data
public class Film {
    private long id;
    private final Set<Long> likesFromUsers = new HashSet<>();

    @NotBlank(message = "Название у фильмы не может отсутствовать")
    private String name;

    @NotBlank(message = "Описание у фильма не должно отсутствовать")
    @Size(max = 200, message = "Описание у фильма не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза у фильмы не может отсутствовать")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность у фильмы не может отсутствовать")
    @Min(value = 1, message = "Продолжительность у фильма не должна быть меньше 1")
    private Long duration;
}