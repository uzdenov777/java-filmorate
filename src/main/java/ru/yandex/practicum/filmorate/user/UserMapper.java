package ru.yandex.practicum.filmorate.user;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.filmorate.user.model.User;
import ru.yandex.practicum.filmorate.user.model.dto.UserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto userDto);

    UserDto toDto(User user);

    List<UserDto> toDtos(Page<User> users);

    User userFromId(Long id);
}