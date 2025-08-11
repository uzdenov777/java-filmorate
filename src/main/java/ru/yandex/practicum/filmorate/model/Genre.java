package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Genre {
    @NotNull(message = "ID не может отсутствовать у Genre")
    private int  id;
    private String name;
}
