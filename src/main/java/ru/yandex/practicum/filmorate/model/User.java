package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User
 * электронная почта не может быть пустой и должна содержать символ @;
 * логин не может быть пустым и содержать пробелы;
 * имя для отображения может быть пустым — в таком случае будет использован логин;
 * дата рождения не может быть в будущем.
 */

@Data
public class User {
    private Long id;
    private String name;
    private final Set<Long> friends = new HashSet<>();

    @NotBlank(message = "Email у пользователя не может отсутствовать")
    @Email(message = "Email у пользователя не верного формата")
    private String email;

    @NotBlank(message = "Login у пользователя не может отсутствовать")
    private String login;

    @Past(message = "Дата рождения у пользователя не должна быть в будущем")
    @NotNull
    private LocalDate birthday;

    public void addFriend(User friendUser) {
        Long friendId = friendUser.getId();
        friends.add(friendId);
    }

    public void removeFriend(User friendUser) {
        Long friendId = friendUser.getId();
        friends.remove(friendId);
    }
}
