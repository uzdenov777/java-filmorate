package ru.yandex.practicum.filmorate.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserService userService;
    User userFirst;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());

        userFirst = new User();
        userFirst.setName("John Doe");
        userFirst.setLogin("login");
        userFirst.setBirthday(LocalDate.now());
        userFirst.setEmail("adsdas009@gmail.com");
    }


    @Test
    @DisplayName("Должен успешно добавить user-а")
    void add_addUser() {
        //given
        List<User> before = userService.getAllUsers();
        assertTrue(before.isEmpty());

        //when
        userService.add(userFirst);

        //then
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals(userFirst, users.get(0));
    }

    @Test
    @DisplayName("Должен успешно обновить user, когда user для обновления был ранее добавлен")
    void update_existingUserToUpdate() {
        //given
        userService.add(userFirst);
        List<User> before = userService.getAllUsers();
        assertEquals(1, before.size());
        assertEquals(userFirst, before.get(0));

        //when
        User newUser = new User();
        newUser.setId(userFirst.getId());
        newUser.setName("newUser");
        newUser.setLogin("login newUser");
        newUser.setBirthday(LocalDate.now().minusDays(1));
        newUser.setEmail("adsdas009@gmail.com");
        userService.update(newUser);

        //then
        List<User> userList = userService.getAllUsers();
        assertEquals(1, userList.size());
        assertEquals(newUser, userList.get(0));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда user-а для обновления с таким ID нету")
    void update_notUpdatedUser_noExistingUserToUpdate() {
        //when+then
        userFirst.setId(1L);
        assertThrows(ResponseStatusException.class, () -> userService.update(userFirst));
    }

    @Test
    @DisplayName("Должен вернуть список всех добавленных фильмов, когда фильмы добавлены")
    void getAllUsers_getNotEmptyListAddUsers() {
        //given
        List<User> before = userService.getAllUsers();
        assertTrue(before.isEmpty());

        //when
        User newUser = new User();
        newUser.setName("newUser");
        newUser.setLogin("login newUser");
        newUser.setBirthday(LocalDate.now().minusDays(1));
        newUser.setEmail("30Сантиметров@.gmail.com");
        userService.add(userFirst);
        userService.add(newUser);

        //then
        List<User> userList = userService.getAllUsers();
        assertEquals(2, userList.size());
        assertEquals(userFirst, userList.get(0));
        assertEquals(newUser, userList.get(1));
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех user-ов, когда user-ы не добавлены")
    void getAllUsers_getEmptyListAddUsers() {
        List<User> userList = userService.getAllUsers();

        assertTrue(userList.isEmpty());
    }
}
