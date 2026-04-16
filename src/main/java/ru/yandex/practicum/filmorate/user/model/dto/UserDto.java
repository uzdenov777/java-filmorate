package ru.yandex.practicum.filmorate.user.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    @Positive(message = "ID у пользователя не должна быть отрицательным")
    private Long id;

    private String name;

    @NotBlank(message = "Login у пользователя не может отсутствовать")
    private String login;

    @NotBlank(message = "Email у пользователя не может отсутствовать")
    @Email(message = "Email у пользователя не верного формата")
    private String email;

    @Past(message = "Дата рождения у пользователя не должна быть в будущем")
    @NotNull(message = "Дата рождения у пользователя не может отсутствовать")
    private LocalDate birthday;
}