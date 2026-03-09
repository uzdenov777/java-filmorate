package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FilmMapper {

    Film toEntity(FilmDto filmDto);

    FilmDto toDto(Film film);

    List<FilmDto> toDtos(List<Film> films);

    List<FilmDto> toDtos(Page<Film> films);

    Film filmFromId(Long id);
}