package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Mpa { // Motion Picture Association- возрастное ограничение
    @NotNull(message = "ID не может отсутствовать у MPA")
    int id;
    String name;
}
