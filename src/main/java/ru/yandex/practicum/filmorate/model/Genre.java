package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Genre {
    @NotNull(message = "ID не может отсутствовать у Genre")
    private int id;

    @NotBlank(message = "Name не может отсутствовать у Genre")
    private String name;
}