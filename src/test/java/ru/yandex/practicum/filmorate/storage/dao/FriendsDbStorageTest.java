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

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class FriendsDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private FriendsDbStorage friendsDbStorage;
    private UserDbStorage userDbStorage;

    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setup() {
        friendsDbStorage = new FriendsDbStorage(jdbcTemplate);
        userDbStorage = new UserDbStorage(jdbcTemplate);

        firstUser = new User();
        firstUser.setName("first");
        firstUser.setEmail("first@email");
        firstUser.setLogin("firstLogin");
        firstUser.setBirthday(LocalDate.now());

        secondUser = new User();
        secondUser.setName("second");
        secondUser.setEmail("second@email");
        secondUser.setLogin("secondLogin");
        secondUser.setBirthday(LocalDate.now());
    }

    @Test
    @DisplayName("Должен добавить друга secondUser для firstUser")
    void addFriend_addingFriends_bothUsersExist() { //проверка односторонней дружбы
        //given
        userDbStorage.add(firstUser);
        userDbStorage.add(secondUser);
        List<User> allUsers = userDbStorage.getAllUsers();
        List<Long> friendIdFirstUserBefore = friendsDbStorage.getAllFriendsIdByUserId(firstUser.getId());
        List<Long> friendsIdSecondUserBefore = friendsDbStorage.getAllFriendsIdByUserId(secondUser.getId());
        assertEquals(2, allUsers.size());
        assertTrue(friendIdFirstUserBefore.isEmpty());
        assertTrue(friendsIdSecondUserBefore.isEmpty());

        //when
        friendsDbStorage.addFriend(firstUser.getId(), secondUser.getId());

        //then
        List<Long> friendIdFirstUserAfter = friendsDbStorage.getAllFriendsIdByUserId(firstUser.getId());
        List<Long> friendsIdSecondUserAfter = friendsDbStorage.getAllFriendsIdByUserId(secondUser.getId());
        assertEquals(secondUser.getId(), friendIdFirstUserAfter.get(0));
        assertTrue(friendsIdSecondUserAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда нового друга нет в БД")
    void addFriend_notAddingFriends_secondUserNotExist() { //проверка односторонней дружбы
        //given
        userDbStorage.add(firstUser);
        List<User> allUsers = userDbStorage.getAllUsers();
        assertEquals(1, allUsers.size());

        //when+then
        Long idUserNotExist = 777L;
        assertThrows(DataIntegrityViolationException.class, () -> friendsDbStorage.addFriend(firstUser.getId(), idUserNotExist));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда пользователя, которому добавляем друга нет в БД")
    void addFriend_notAddingFriends_firstUserNotExist() { //проверка односторонней дружбы
        //given
        userDbStorage.add(secondUser);
        List<User> allUsers = userDbStorage.getAllUsers();
        assertEquals(1, allUsers.size());

        //when+then
        Long idUserNotExist = 777L;
        assertThrows(DataIntegrityViolationException.class, () -> friendsDbStorage.addFriend(idUserNotExist, secondUser.getId()));
    }

    @Test
    @DisplayName("Должен удалить друга secondUser для firstUser")
    void removeFriend_removeFriend_bothUsersExist() { //проверка односторонней дружбы
        //given
        userDbStorage.add(firstUser);
        userDbStorage.add(secondUser);
        friendsDbStorage.addFriend(firstUser.getId(), secondUser.getId());
        List<Long> friendIdFirstUserBefore = friendsDbStorage.getAllFriendsIdByUserId(firstUser.getId());
        List<Long> friendsIdSecondUserBefore = friendsDbStorage.getAllFriendsIdByUserId(secondUser.getId());
        assertEquals(secondUser.getId(), friendIdFirstUserBefore.get(0));
        assertTrue(friendsIdSecondUserBefore.isEmpty());

        //when
        friendsDbStorage.removeFriend(firstUser.getId(), secondUser.getId());

        //then
        List<Long> friendIdFirstUserAfter = friendsDbStorage.getAllFriendsIdByUserId(firstUser.getId());
        List<Long> friendsIdSecondUserAfter = friendsDbStorage.getAllFriendsIdByUserId(secondUser.getId());
        assertTrue(friendIdFirstUserAfter.isEmpty());
        assertTrue(friendsIdSecondUserAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда друга для удаления нет в БД")
    void removeFriend_removeFriend_secondUserNotExist() { //проверка односторонней дружбы
        //given
        userDbStorage.add(firstUser);
        List<User> allUsers = userDbStorage.getAllUsers();
        assertEquals(1, allUsers.size());

        //when+then
        Long idUserNotExist = 777L;
        assertThrows(DataIntegrityViolationException.class, () -> friendsDbStorage.addFriend(firstUser.getId(), idUserNotExist));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда пользователя, которому удаляем друга нет в БД")
    void removeFriend_removeFriend_firstUserNotExist() { //проверка односторонней дружбы
        //given
        userDbStorage.add(secondUser);
        List<User> allUsers = userDbStorage.getAllUsers();
        assertEquals(1, allUsers.size());

        //when+then
        Long idUserNotExist = 777L;
        assertThrows(DataIntegrityViolationException.class, () -> friendsDbStorage.addFriend(idUserNotExist, secondUser.getId()));
    }

    @Test
    @DisplayName("Должен вернуть список ID друзей, когда есть друзья у пользователя")
    void getAllFriendsIdByUserId_friendsAdded() {
        //given
        userDbStorage.add(firstUser);
        userDbStorage.add(secondUser);
        friendsDbStorage.addFriend(firstUser.getId(), secondUser.getId());

        //when
        List<Long> friendIdFirstUserBefore = friendsDbStorage.getAllFriendsIdByUserId(firstUser.getId());

        //then
        assertEquals(secondUser.getId(), friendIdFirstUserBefore.get(0));
    }

    @Test
    @DisplayName("Должен вернуть пустой список ID друзей, когда у пользователя нет друзей")
    void getAllFriendsIdByUserId_friendsNotAdded() {
        //given
        userDbStorage.add(firstUser);

        //when
        List<Long> friendIdFirstUserBefore = friendsDbStorage.getAllFriendsIdByUserId(firstUser.getId());

        //then
        assertTrue(friendIdFirstUserBefore.isEmpty());
    }
}