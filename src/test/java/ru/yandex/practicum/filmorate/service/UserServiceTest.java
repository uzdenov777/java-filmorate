package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class UserServiceTest {
    private final JdbcTemplate jdbcTemplate;

    private UserService userService;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(new UserDbStorage(jdbcTemplate), new FriendsServer(new FriendsDbStorage(jdbcTemplate)));

        firstUser = new User();
        firstUser.setName("John Doe");
        firstUser.setLogin("login");
        firstUser.setBirthday(LocalDate.now());
        firstUser.setEmail("adsdas009@gmail.com");

        secondUser = new User();
        secondUser.setName("Dani");
        secondUser.setLogin("777");
        secondUser.setBirthday(LocalDate.now());
        secondUser.setEmail("google@icloud.com");
    }

    @Test
    @DisplayName("Должен успешно добавить user-а")
    void add_addUser() {
        //given
        List<User> before = userService.getAllUsers();
        assertTrue(before.isEmpty());

        //when
        userService.add(firstUser);

        //then
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals(firstUser, users.get(0));
    }

    @Test
    @DisplayName("Должен успешно обновить user, когда user для обновления был ранее добавлен")
    void update_existingUserToUpdate() {
        //given
        userService.add(firstUser);
        List<User> before = userService.getAllUsers();
        assertEquals(1, before.size());
        assertEquals(firstUser, before.get(0));

        //when
        User newUser = new User();
        newUser.setId(firstUser.getId());
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
        firstUser.setId(1L);
        assertThrows(ResponseStatusException.class, () -> userService.update(firstUser));
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
        userService.add(firstUser);
        userService.add(newUser);

        //then
        List<User> userList = userService.getAllUsers();
        assertEquals(2, userList.size());
        assertEquals(firstUser, userList.get(0));
        assertEquals(newUser, userList.get(1));
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех user-ов, когда user-ы не добавлены")
    void getAllUsers_getEmptyListAddUsers() {
        List<User> userList = userService.getAllUsers();

        assertTrue(userList.isEmpty());
    }

    @Test
    @DisplayName("Должны успешно добавиться друга пользователю firstUser, когда оба пользователя сущ и не равны")
    void addFriend_usersExistAndNotEqual() { //проверка односторонней дружбы
        //given
        userService.add(firstUser);
        userService.add(secondUser);
        List<User> friendsFirstUserBefore = userService.getAllFriendsByUserId(firstUser.getId());
        List<User> friendsSecondUserBefore = userService.getAllFriendsByUserId(secondUser.getId());
        assertTrue(friendsFirstUserBefore.isEmpty());
        assertTrue(friendsSecondUserBefore.isEmpty());

        //when
        long idFirstUser = firstUser.getId();
        long idSecondUser = secondUser.getId();
        userService.addFriend(idFirstUser, idSecondUser);

        //then
        List<User> friendsFirstUserAfter = userService.getAllFriendsByUserId(firstUser.getId());
        List<User> friendsSecondUserAfter = userService.getAllFriendsByUserId(secondUser.getId());
        assertEquals(idSecondUser, friendsFirstUserAfter.get(0).getId());
        assertTrue(friendsSecondUserAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда первый пользователь не добавлен")
    void addFriend_throwResponseStatusException_notExistFirstUser() { //проверка односторонней дружбы
        //given
        userService.add(secondUser);

        //when+then
        long idUserNotExits = 777L;
        long idSecondUser = secondUser.getId();
        assertThrows(ResponseStatusException.class, () -> userService.addFriend(idUserNotExits, idSecondUser));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда второй пользователь не добавлен")
    void addFriend_throwResponseStatusException_notExistSecondUser() { //проверка односторонней дружбы
        //given
        userService.add(firstUser);

        //when+then
        long idFirstUser = firstUser.getId();
        long idUserNotExits = 777L;
        assertThrows(ResponseStatusException.class, () -> userService.addFriend(idFirstUser, idUserNotExits));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда первый пользователь хочет добавить себя же в друзья")
    void addFriend_throwResponseStatusException_userIsSelf() { //проверка односторонней дружбы
        //given
        userService.add(firstUser);
        Set<Long> friendsFirstUserBefore = firstUser.getFriends();
        assertTrue(friendsFirstUserBefore.isEmpty());

        //when+then
        long idFirstUser = firstUser.getId();
        assertThrows(ResponseStatusException.class, () -> userService.addFriend(idFirstUser, idFirstUser));
    }

    @Test
    @DisplayName("Должны успешно удалить друга у пользователя из друзей, когда оба пользователя сущ и не равны")
    void removeFriend_usersExistAndNotEqual() { //проверка односторонней дружбы
        //given
        userService.add(firstUser);
        userService.add(secondUser);
        long idFirstUser = firstUser.getId();
        long idSecondUser = secondUser.getId();
        userService.addFriend(idFirstUser, idSecondUser);
        List<User> friendsFirstUserBefore = userService.getAllFriendsByUserId(firstUser.getId());
        List<User> friendsSecondUserBefore = userService.getAllFriendsByUserId(secondUser.getId());
        assertEquals(idSecondUser, friendsFirstUserBefore.get(0).getId());
        assertTrue(friendsSecondUserBefore.isEmpty());

        //when
        userService.removeFriend(idFirstUser, idSecondUser);

        //then
        List<User> friendsFirstUserAfter = userService.getAllFriendsByUserId(firstUser.getId());
        List<User> friendsSecondUserAfter = userService.getAllFriendsByUserId(secondUser.getId());
        assertTrue(friendsFirstUserAfter.isEmpty());
        assertTrue(friendsSecondUserAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда первый пользователь не добавлен")
    void removeFriend_throwResponseStatusException_notExistFirstUser() { //проверка односторонней дружбы
        //given
        userService.add(secondUser);
        long idUserNotExits = 777L;
        long idSecondUser = secondUser.getId();

        //when+then
        assertThrows(ResponseStatusException.class, () -> userService.removeFriend(idUserNotExits, idSecondUser));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда второй пользователь не добавлен")
    void removeFriend_throwResponseStatusException_notExistSecondUser() { //проверка односторонней дружбы
        //given
        userService.add(firstUser);

        //when+then
        long idNotExistUser = 10;
        long idFirstUser = firstUser.getId();
        assertThrows(ResponseStatusException.class, () -> userService.removeFriend(idFirstUser, idNotExistUser));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда первый пользователь не добавлен")
    void removeFriend_throwResponseStatusException_userIsSelf() { //проверка односторонней дружбы
        //given
        userService.add(firstUser);

        //when+then
        long idFirstUser = firstUser.getId();
        assertThrows(ResponseStatusException.class, () -> userService.removeFriend(idFirstUser, idFirstUser));
    }

    @Test
    @DisplayName("Должен вернут список друзей, которые добавлены, когда пользователь тоже добавлен")
    void getAllFriendsByUserId_returnNotEmptyList_AddingExistFriendAndUserExist() {
        //given
        userService.add(firstUser);
        userService.add(secondUser);
        long idFirst = firstUser.getId();
        long idSecond = secondUser.getId();
        userService.addFriend(idFirst, idSecond);

        //when
        List<User> friends = userService.getAllFriendsByUserId(idFirst);

        //then
        assertEquals(1, friends.size());
        assertEquals(secondUser, friends.get(0));
    }

    @Test
    @DisplayName("Должен вернуть пустой список друзей пользователя, когда друзья еще не добавлены, а пользователь существует")
    void getAllFriendsByUserId_returnEmptyList_notAddingFriendAndUserExist() {
        //given
        userService.add(firstUser);
        long idFirst = firstUser.getId();

        //when
        List<User> friends = userService.getAllFriendsByUserId(idFirst);

        //then
        assertTrue(friends.isEmpty());
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда пользователь не найден")
    void getAllFriendsByUserId_throwResponseStatusException_userNotExist() {
        //when+then
        long notExistIdUser = 56;
        assertThrows(ResponseStatusException.class, () -> userService.getAllFriendsByUserId(notExistIdUser));
    }

    @Test
    @DisplayName("Должен вернуть не пустой список общих друзей, когда они есть")
    void getListMutualFriends_returnNotEmptyList_existMutualFriends() {
        //given
        User mutualFriend = new User();
        mutualFriend.setName("mutualFriend");
        mutualFriend.setLogin("mutualFriend");
        mutualFriend.setEmail("mut@mail.com");
        mutualFriend.setBirthday(LocalDate.now());

        userService.add(firstUser);
        userService.add(secondUser);
        userService.add(mutualFriend);

        long idFirst = firstUser.getId();
        long idSecond = secondUser.getId();
        List<User> mutualFriendsBefore = userService.getListMutualFriends(idFirst, idSecond);
        assertTrue(mutualFriendsBefore.isEmpty());

        //when
        long idMutual = mutualFriend.getId();
        userService.addFriend(idFirst, idMutual);
        userService.addFriend(idSecond, idMutual);
        List<User> mutualFriendsAfter = userService.getListMutualFriends(idFirst, idSecond);

        //then
        assertEquals(1, mutualFriendsAfter.size());
        assertEquals(idMutual, mutualFriendsAfter.get(0).getId());
    }

    @Test
    @DisplayName("Должен вернуть пустой список общих друзей, когда нет общих друзей")
    void getListMutualFriends_returnEmptyList_notExistMutualFriends() {
        //given
        User friendDinis = new User();
        friendDinis.setName("friendDinis");
        friendDinis.setLogin("loginDinis");
        friendDinis.setEmail("emailDinis@.com");
        friendDinis.setBirthday(LocalDate.now());
        User friendAdam = new User();
        friendAdam.setName("adam");
        friendAdam.setLogin("adam");
        friendAdam.setEmail("emailAdam@.com");
        friendAdam.setBirthday(LocalDate.now());

        userService.add(firstUser);
        userService.add(secondUser);
        userService.add(friendDinis);
        userService.add(friendAdam);

        long idFirst = firstUser.getId();
        long idSecond = secondUser.getId();
        List<User> mutualFriendsBefore = userService.getListMutualFriends(idFirst, idSecond);
        assertTrue(mutualFriendsBefore.isEmpty());

        //when
        long idFriendDinis = friendDinis.getId();
        long idFriendAdam = friendAdam.getId();
        userService.addFriend(idFirst, idFriendDinis);
        userService.addFriend(idSecond, idFriendAdam);
        List<User> mutualFriendsAfter = userService.getListMutualFriends(idFirst, idSecond);

        //then
        assertTrue(mutualFriendsAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда первый пользователь не существует")
    void getListMutualFriends_throwResponseStatusException_firstUserNotExist() {
        //given
        userService.add(secondUser);

        //when+then
        long idNotExistFirstUser = 46;
        long idSecond = secondUser.getId();
        assertThrows(ResponseStatusException.class, () -> userService.getListMutualFriends(idNotExistFirstUser, idSecond));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда второй пользователь не существует")
    void getListMutualFriends_throwResponseStatusException_secondUserNotExist() {
        //given
        userService.add(firstUser);

        //when+then
        long idNotExistSecondUser = 46;
        long idFirst = firstUser.getId();
        assertThrows(ResponseStatusException.class, () -> userService.getListMutualFriends(idFirst, idNotExistSecondUser));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда пользователь ищет общих друзей у самого себя")
    void getListMutualFriends_throwResponseStatusException_usersAreSam() {
        //given
        userService.add(firstUser);

        //when+then
        long idFirst = firstUser.getId();
        assertThrows(ResponseStatusException.class, () -> userService.getListMutualFriends(idFirst, idFirst));
    }
}