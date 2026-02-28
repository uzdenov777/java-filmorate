package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmGenresService;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FilmMapper {

    Film toEntity(FilmDto filmDto);

    @Mapping(target = "genres", source = "genres")
    FilmDto toDto(Film film, List<Genre> genres);

    default List<FilmDto> toDtos(List<Film> films, FilmGenresService genresService) {

        List<FilmDto> filmDtos = new ArrayList<>();

        for (Film film : films) {

            List<Genre> genres = genresService.getGenresByFilm(film);
            FilmDto dto = toDto(film, genres);
            filmDtos.add(dto);
        }

        return filmDtos;
    }
}