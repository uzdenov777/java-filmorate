package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private UserDbStorage userDbStorage;
    private User firstUser;

    @BeforeEach
    void setUp() {
        userDbStorage = new UserDbStorage(jdbcTemplate);

        firstUser = new User();
        firstUser.setName("John");
        firstUser.setEmail("john@gmail");
        firstUser.setLogin("johnLogin");
        firstUser.setBirthday(LocalDate.now());
    }

    @Test
    @DisplayName("Должен успешно добавить пользователя и сохранить ему новый ID")
    void add_addingUser() {
        //given
        Long firstUserId = firstUser.getId();
        List<User> usersBefore = userDbStorage.findAll();
        assertTrue(usersBefore.isEmpty());
        assertNull(firstUserId);// пока фильм еще не добавлен у него нет ID

        //when
        userDbStorage.add(firstUser);

        //then
        List<User> usersAfter = userDbStorage.findAll();
        User userAfter = usersAfter.get(0);
        assertEquals(firstUser, userAfter);
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении нового пользователя, когда у пользователя отсутствует name")
    void add_notAddingUser_nameNull() {
        //given
        List<User> usersBefore = userDbStorage.findAll();
        assertTrue(usersBefore.isEmpty());

        //when
        firstUser.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> userDbStorage.add(firstUser));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении нового пользователя, когда у пользователя отсутствует email")
    void add_notAddingUser_emailNull() {
        //given
        List<User> usersBefore = userDbStorage.findAll();
        assertTrue(usersBefore.isEmpty());

        //when
        firstUser.setEmail(null);
        assertThrows(DataIntegrityViolationException.class, () -> userDbStorage.add(firstUser));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении нового пользователя, когда у пользователя отсутствует login")
    void add_notAddingUser_loginNull() {
        //given
        List<User> usersBefore = userDbStorage.findAll();
        assertTrue(usersBefore.isEmpty());

        //when
        firstUser.setLogin(null);
        assertThrows(DataIntegrityViolationException.class, () -> userDbStorage.add(firstUser));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении нового пользователя, когда у пользователя отсутствует Birthday")
    void add_notAddingUser_birthdayNull() {
        //given
        List<User> usersBefore = userDbStorage.findAll();
        assertTrue(usersBefore.isEmpty());

        //when
        firstUser.setBirthday(null);
        assertThrows(DataIntegrityViolationException.class, () -> userDbStorage.add(firstUser));
    }

    @Test
    @DisplayName("Должен обновить фильм")
    void update() {
        //given

        userDbStorage.add(firstUser);
        Long firstUserId = firstUser.getId();
        List<User> usersBefore = userDbStorage.findAll();
        User userBefore = usersBefore.get(0);
        assertEquals(firstUser, userBefore);

        //when
        User newUser = new User();
        newUser.setId(firstUserId);
        newUser.setName("New User");
        newUser.setEmail("newUser@gmail");
        newUser.setLogin("newUserLogin");
        newUser.setBirthday(LocalDate.now());
        userDbStorage.update(newUser);

        //then
        List<User> usersAfter = userDbStorage.findAll();
        Long newUserId = newUser.getId();
        User userAfter = usersAfter.get(0);
        assertEquals(newUserId, firstUserId);
        assertEquals(newUser, userAfter);
    }

    @Test
    @DisplayName("Должен вернуть user по ID, когда user добавлен заранее")
    void findById_userExists() {
        //given
        userDbStorage.add(firstUser);
        List<User> usersBefore = userDbStorage.findAll();
        assertEquals(firstUser, usersBefore.get(0));

        //when
        Long userId = firstUser.getId();
        Optional<User> userOpt = userDbStorage.findById(userId);
        User resUser = userOpt.get();

        //then
        assertEquals(firstUser, resUser);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional, когда пользователь по ID не найдено в БД")
    void findById_userNotExists() {
        //given
        List<User> usersBefore = userDbStorage.findAll();
        assertTrue(usersBefore.isEmpty());

        //when
        long idUserNotExists = 777L;
        Optional<User> userOpt = userDbStorage.findById(idUserNotExists);

        //then
        assertTrue(userOpt.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть не пустой список всех пользователей, когда user-ы добавлены заранее")
    void findAll_usersExists() {
        //given
        userDbStorage.add(firstUser);

        //when
        List<User> usersRes = userDbStorage.findAll();

        //then
        assertEquals(firstUser, usersRes.get(0));
    }

    @Test
    @DisplayName("Должен вернуть не пустой список всех пользователей, когда user-ы добавлены заранее")
    void findAll_usersNotExists() {
        //when
        List<User> usersRes = userDbStorage.findAll();

        //then
        assertTrue(usersRes.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть true, когда запрашиваемый фильм есть в БД")
    void isUserExists_userExists() {
        //given
        userDbStorage.add(firstUser);

        //when
        long userId = firstUser.getId();
        boolean exists = userDbStorage.isUserExists(userId);

        //then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Должен вернуть false, когда запрашиваемый фильм не добавлен в БД")
    void isUserExists_userNotExists() {

        //when
        long userIdNotExists = 777;
        boolean exists = userDbStorage.isUserExists(userIdNotExists);

        //then
        assertFalse(exists);
    }
}