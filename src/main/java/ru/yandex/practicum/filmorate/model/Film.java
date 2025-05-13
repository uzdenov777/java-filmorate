package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 * название не может быть пустым;
 * максимальная длина описания — 200 символов;
 * дата релиза — не раньше 28 декабря 1895 года;
 * продолжительность фильма должна быть положительной.
 */
@Data
public class Film {
    @NotNull(message = "ID у фильмы не может отсутствовать")
    private int id;
    @NotBlank(message = "Название у фильмы не может отсутствовать")
    private String name;
    @NotBlank( message = "Описание у фильма не должно отсутствовать")
    @Size(max = 200, message = "Описание у фильма не должно превышать 200 символов")
    private String description;
    @NotNull(message = "Дата релиза у фильмы не может отсутствовать")
    private LocalDate releaseDate;
    @NotNull(message = "Продолжительность у фильмы не может отсутствовать")
    @PositiveOrZero
    private Duration duration;
}
