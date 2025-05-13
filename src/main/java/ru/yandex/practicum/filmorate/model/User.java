package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/*User
электронная почта не может быть пустой и должна содержать символ @;
логин не может быть пустым и содержать пробелы;
имя для отображения может быть пустым — в таком случае будет использован логин;
дата рождения не может быть в будущем.
* */
@Data
public class User {
    @Min(1)
    @NotNull(message = "ID у пользователя не может отсутствовать")
    private int id;
    @NotBlank(message = "Email у пользователя не может отсутствовать")
    @Email(message = "Email у пользователя не верного формата")
    private String email;
    @NotBlank(message = "Login у пользователя не может отсутствовать")
    private String login;
    private String name;
    @Past(message = "Дата рождения у пользователя не должна быть в будущем")
    private LocalDate birthday;

    public void setDisplayName(String name, String login) {

         this.name = (name == null || name.isBlank()) ? login : name;
    }
}
