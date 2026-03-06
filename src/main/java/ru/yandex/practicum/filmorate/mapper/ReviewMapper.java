package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.dto.ReviewDto;

@Mapper(componentModel = "spring", uses = {UserMapper.class, FilmMapper.class})
public interface ReviewMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "filmId", source = "film.id")
    @Mapping(target = "isPositive", source = "isPositive")
    @Mapping(target = "id", source = "id")
    ReviewDto toDto(Review review);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "film", source = "filmId")
    @Mapping(target = "isPositive", source = "isPositive")

    Review toEntity(ReviewDto reviewDto);
}