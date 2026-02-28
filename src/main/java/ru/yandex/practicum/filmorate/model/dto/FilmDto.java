package ru.yandex.practicum.filmorate.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private List<Genre> genres = new ArrayList<>();
}