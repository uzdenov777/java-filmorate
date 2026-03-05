package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.dto.DirectorDto;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface DirectorMapper {

    Director toEntity(DirectorDto directorDto);

    DirectorDto toDto(Director director);

    Set<DirectorDto> toDtos(Page<Director> directors);
}