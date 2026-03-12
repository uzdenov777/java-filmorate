package ru.yandex.practicum.filmorate.director.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DirectorDto {

    private Long id;

    @NotEmpty(message = "У режиссера не может отсутствовать имя")
    private String name;
}