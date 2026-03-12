package ru.yandex.practicum.filmorate.director;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.filmorate.director.model.Director;
import ru.yandex.practicum.filmorate.director.model.DirectorDto;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface DirectorMapper {

    Director toEntity(DirectorDto directorDto);

    DirectorDto toDto(Director director);

    Set<DirectorDto> toDtos(Page<Director> directors);
}