package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 * Название не может быть пустым;
 * максимальная длина описания — 200 символов;
 * дата релиза — не раньше 28 декабря 1895 года;
 * продолжительность фильма должна быть положительной.
 */
@Data
public class Film {
    private int id;
    @NotBlank(message = "Название у фильмы не может отсутствовать")
    private String name;
    @NotBlank(message = "Описание у фильма не должно отсутствовать")
    @Size(max = 200, message = "Описание у фильма не должно превышать 200 символов")
    private String description;
    @NotNull(message = "Дата релиза у фильмы не может отсутствовать")
    private LocalDate releaseDate;
    @NotNull(message = "Продолжительность у фильмы не может отсутствовать")
    @Min(1)
    private long duration;
}
