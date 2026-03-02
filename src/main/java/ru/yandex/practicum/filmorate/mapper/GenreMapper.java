package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.dto.GenreDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    Genre toEntity(GenreDto genreNoFilm);

    GenreDto toDto(Genre genre);

    List<GenreDto> toDtos(List<Genre> genres);

    List<GenreDto> toDtos(Page<Genre> genres);
}