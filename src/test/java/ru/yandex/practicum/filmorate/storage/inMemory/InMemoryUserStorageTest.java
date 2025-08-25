package ru.yandex.practicum.filmorate.storage.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {
    InMemoryUserStorage userStorage;
    User userFirst;
    User userSecond;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userFirst = new User();
        userFirst.setName("John Doe");
        userFirst.setLogin("login");
        userFirst.setBirthday(LocalDate.now());
        userFirst.setEmail("adsdas009@gmail.com");

        userSecond = new User();
        userSecond.setName("Jane Doe");
        userSecond.setLogin("login");
        userSecond.setBirthday(LocalDate.now());
        userSecond.setEmail("fgdfg@gmail.com");
    }

    @Test
    @DisplayName("Должен успешно добавить пользователя")
    void add_addingUser() {
        //given
        List<User> allUsersBefore = userStorage.getAllUsers();
        assertTrue(allUsersBefore.isEmpty());

        //when
        userStorage.add(userFirst);

        //then
        List<User> allUsersAfter = userStorage.getAllUsers();
        assertTrue(allUsersAfter.contains(userFirst));
    }

    @Test
    @DisplayName("Должен успешно обновить пользователя, когда пользователь с таким id есть")
    void update_mustUpdateUser_userExists() {
        //given
        userStorage.add(userFirst);
        List<User> allUsersBefore = userStorage.getAllUsers();
        assertTrue(allUsersBefore.contains(userFirst));

        //when
        userSecond.setId(userFirst.getId());
        userStorage.update(userSecond);

        //then
        List<User> allUsersAfter = userStorage.getAllUsers();
        assertTrue(allUsersAfter.contains(userSecond));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда пользователя для обновления с таким id нету")
    void update_throwResponseStatusException_userNotExist() {
        //given
        List<User> allUsersBefore = userStorage.getAllUsers();
        assertTrue(allUsersBefore.isEmpty());

        //when+then
        userFirst.setId(45L);
        assertThrows(ResponseStatusException.class, () -> userStorage.update(userFirst));
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех пользователей, когда пользователей еще не добавили")
    void getAllUsers_returnEmptyList_notAddingUsers() {
        //when+then
        List<User> allUsers = userStorage.getAllUsers();
        assertTrue(allUsers.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть список всех пользователей c одним User, когда добавлен один пользователь")
    void getAllUsers_returnListSize1_AddingUser() {
        //given
        List<User> allUsersBefore = userStorage.getAllUsers();
        assertTrue(allUsersBefore.isEmpty());

        //when
        userStorage.add(userFirst);
        List<User> allUsersAfter = userStorage.getAllUsers();

        //then
        assertEquals(1, allUsersAfter.size());
        assertTrue(allUsersAfter.contains(userFirst));
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех пользователей, когда пользователей еще не добавили")
    void getAllUsers_returnListSize2_AddingUsers() {
        //given
        List<User> allUsersBefore = userStorage.getAllUsers();
        assertTrue(allUsersBefore.isEmpty());

        //when
        userStorage.add(userFirst);
        userStorage.add(userSecond);
        List<User> allUsersAfter = userStorage.getAllUsers();

        //then
        assertEquals(2, allUsersAfter.size());
        assertTrue(allUsersAfter.contains(userFirst));
        assertTrue(allUsersAfter.contains(userSecond));
    }

    @Test
    @DisplayName("Должен успешно вернуть сущность User, когда пользователь по этому id добавлен")
    void getUserById_returnUser_existsUser() {
        //given
        userStorage.add(userFirst);

        //when
        long idUser = userFirst.getId();
        User user = userStorage.getUserById(idUser);

        //then
        assertEquals(userFirst, user);
    }

    @Test
    @DisplayName("Должен вернуть Null, когда пользователь по этому id не добавлен")
    void getUserById_returnNull_notExistsUser() {
        //when
        long idNotExists = 755;
        User user = userStorage.getUserById(idNotExists);

        //then
        assertNull(user);
    }
}