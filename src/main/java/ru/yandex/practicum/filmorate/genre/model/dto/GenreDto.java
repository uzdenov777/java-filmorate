package ru.yandex.practicum.filmorate.genre.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GenreDto {

    private Long id;

    private String name;
}