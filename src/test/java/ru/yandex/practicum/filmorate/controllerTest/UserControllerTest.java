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
    User userOne;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
        userOne = new User();
        userOne.setName("John Doe");
        userOne.setLogin("login");
        userOne.setBirthday(LocalDate.now());
        userOne.setEmail("adsdas009@gmail.com");
    }

    @Test
    @DisplayName("Должен устанавливать новый ID для пользователя при каждом добавлении")
    public void getFilmId() {
        User newUser = new User();
        newUser.setName("John Doe");
        newUser.setLogin("login");
        newUser.setBirthday(LocalDate.now());
        newUser.setEmail("adsdas009@gmail.com");
        assertEquals(0, userOne.getId());
        assertEquals(0, newUser.getId());

        userController.add(userOne);
        userController.add(newUser);

        int idUser = userOne.getId();
        int idNewUser = newUser.getId();
        assertEquals(1, idUser);
        assertEquals(2, idNewUser);
    }

    @Test
    @DisplayName("Должен успешно добавить user-а")
    public void add_addUser() {
        List<User> before = userController.getAllUsers();
        assertTrue(before.isEmpty());

        userController.add(userOne);

        List<User> users = userController.getAllUsers();
        assertEquals(1, users.size());
        assertEquals(userOne, users.get(0));
    }

    @Test
    @DisplayName("Должен успешно обновить user, когда user для обновления был ранее добавлен")
    public void update_existingUserToUpdate() {
        userController.add(userOne);
        List<User> before = userController.getAllUsers();
        assertEquals(1, before.size());
        assertEquals(userOne, before.get(0));

        User newUser = new User();
        newUser.setId(userOne.getId());
        newUser.setName("newUser");
        newUser.setLogin("login newUser");
        newUser.setBirthday(LocalDate.now().minusDays(1));
        newUser.setEmail("adsdas009@gmail.com");
        userController.update(newUser);

        List<User> userList = userController.getAllUsers();
        assertEquals(1, userList.size());
        assertEquals(newUser, userList.get(0));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда user-а для обновления с таким ID нету")
    public void update_notUpdatedUser_noExistingUserToUpdate() {
        assertThrows(ResponseStatusException.class, () -> userController.update(userOne));
    }

    @Test
    @DisplayName("Должен вернуть список всех добавленных фильмов, когда фильмы добавлены")
    public void getAllUsers_getNotEmptyListAddUsers() {
        List<User> before = userController.getAllUsers();
        assertTrue(before.isEmpty());

        User newUser = new User();
        newUser.setName("newUser");
        newUser.setLogin("login newUser");
        newUser.setBirthday(LocalDate.now().minusDays(1));
        newUser.setEmail("30Сантиметров@.gmail.com");

        userController.add(userOne);
        userController.add(newUser);

        List<User> userList = userController.getAllUsers();
        assertEquals(2, userList.size());
        assertEquals(userOne, userList.get(0));
        assertEquals(newUser, userList.get(1));
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех user-ов, когда user-ы не добавлены")
    public void getAllUsers_getEmptyListAddUsers() {
        List<User> userList = userController.getAllUsers();

        assertTrue(userList.isEmpty());
    }
}
