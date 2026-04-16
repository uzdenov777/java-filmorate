package ru.yandex.practicum.filmorate.director.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DirectorDto {

    private Long id;

    @NotEmpty(message = "У режиссера не может отсутствовать имя")
    private String name;
}