package ru.yandex.practicum.filmorate.user.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private Long id;

    private String name;

    @NotBlank(message = "Email у пользователя не может отсутствовать")
    @Email(message = "Email у пользователя не верного формата")
    private String email;

    @NotBlank(message = "Login у пользователя не может отсутствовать")
    private String login;

    @Past(message = "Дата рождения у пользователя не должна быть в будущем")
    @NotNull(message = "Дата рождения у пользователя не может отсутствовать")
    private LocalDate birthday;
}