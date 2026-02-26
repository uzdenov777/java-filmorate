package ru.yandex.practicum.filmorate.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FilmRequest {

    @Positive(message = "ID у фильма не должна быть отрицательным")
    Long id;

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
}