package ru.yandex.practicum.filmorate.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    UserController userController;
    User user;

    @BeforeEach
    public void setUp() {
        userController = new UserController();

        user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setLogin("login");
        user.setBirthday(LocalDate.now());
        user.setEmail("adsdas009@.gmail.com");
    }

    @Test
    @DisplayName("Должен возвращать новый ID для фильма при каждом вызове")
    public void getFilmId() {
        int idNewOne = userController.getNewId();
        assertEquals(1, idNewOne);

        int idNewTwo = userController.getNewId();
        assertEquals(2, idNewTwo);

        int idNewThree = userController.getNewId();
        assertEquals(3, idNewThree);
    }

    @Test
    @DisplayName("Должен успешно добавить user-а")
    public void add_addUser() {
        userController.add(user);
        List<User> users = userController.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    @DisplayName("Должен успешно обновить user, когда user для обновления был ранее добавлен")
    public void update_existingUserToUpdate() {
        userController.add(user);

        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setName("newUser");
        newUser.setLogin("login newUser");
        newUser.setBirthday(LocalDate.now().minusDays(1));
        newUser.setEmail("adsdas009@.gmail.com");

        userController.update(newUser);
        List<User> userList = userController.getAllUsers();

        assertEquals(1, userList.size());
        assertEquals(newUser, userList.get(0));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда user-а для обновления с таким ID нету")
    public void update_notUpdatedUser_noExistingUserToUpdate() {
        assertThrows(ResponseStatusException.class, () -> userController.update(user));
    }

    @Test
    @DisplayName("Должен вернуть список всех добавленных фильмов, когда фильмы добавлены")
    public void getAllUsers_getNotEmptyListAddUsers() {
        User newUser = new User();
        newUser.setId(userController.getNewId());
        newUser.setName("newUser");
        newUser.setLogin("login newUser");
        newUser.setBirthday(LocalDate.now().minusDays(1));
        newUser.setEmail("30Сантиметров@.gmail.com");

        userController.add(user);
        userController.add(newUser);
        List<User> userList = userController.getAllUsers();

        assertEquals(2, userList.size());
        assertEquals(user, userList.get(0));
        assertEquals(newUser, userList.get(1));
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех user-ов, когда user-ы не добавлены")
    public void getAllUsers_getEmptyListAddUsers() {
        List<User> userList = userController.getAllUsers();

        assertTrue(userList.isEmpty());
    }
}
