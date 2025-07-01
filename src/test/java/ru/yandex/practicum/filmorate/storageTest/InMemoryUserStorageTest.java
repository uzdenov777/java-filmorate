package ru.yandex.practicum.filmorate.storageTest;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

class InMemoryUserStorageTest {
    UserController userController;
    User userFirst;

    @BeforeEach
    void setUp() {
        UserService userService = new UserService(new InMemoryUserStorage()); // или твоя реализация
        userController = new UserController(userService);
        userFirst = new User();
        userFirst.setName("John Doe");
        userFirst.setLogin("login");
        userFirst.setBirthday(LocalDate.now());
        userFirst.setEmail("adsdas009@gmail.com");
    }
}
