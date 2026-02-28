package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * User
 * электронная почта не может быть пустой и должна содержать символ @;
 * логин не может быть пустым и содержать пробелы;
 * имя для отображения может быть пустым — в таком случае будет использован логин;
 * дата рождения не может быть в будущем.
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "login")
    private String login;

    @Column(name = "birthday")
    private LocalDate birthday;
}