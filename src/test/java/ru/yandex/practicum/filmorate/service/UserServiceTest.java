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
        User returnedFirstUser = users.get(0);
        assertEquals(1, users.size());
        assertEquals(firstUser, returnedFirstUser);
    }

    @Test
    @DisplayName("Должен успешно обновить user, когда user для обновления был ранее добавлен")
    void update_existingUserToUpdate() {
        //given
        userService.add(firstUser);
        List<User> before = userService.getAllUsers();
        User returnedFirstUser = before.get(0);
        assertEquals(1, before.size());
        assertEquals(firstUser, returnedFirstUser);

        //when
        Long firstUserId = firstUser.getId();
        User newUser = new User();
        newUser.setId(firstUserId);
        newUser.setName("newUser");
        newUser.setLogin("login newUser");
        newUser.setBirthday(LocalDate.now().minusDays(1));
        newUser.setEmail("adsdas009@gmail.com");
        userService.update(newUser);

        //then
        List<User> userList = userService.getAllUsers();
        User returnedNewUser = userList.get(0);
        assertEquals(1, userList.size());
        assertEquals(newUser, returnedNewUser);
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда user-а для обновления с таким ID нету")
    void update_notUpdatedUser_noExistingUserToUpdate() {
        //when+then
        firstUser.setId(1L);
        assertThrows(ResponseStatusException.class, () -> userService.update(firstUser));
    }

    @Test
    @DisplayName("Должен вернуть список всех добавленных пользователей, когда пользователи добавлены")
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
        List<User> userList = userService.getAllUsers();

        //then
        User returnedFirstUser = userList.get(0);
        User returnedNewUser = userList.get(1);
        assertEquals(2, userList.size());
        assertEquals(firstUser, returnedFirstUser);
        assertEquals(newUser, returnedNewUser);
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
        Long firstUserId = firstUser.getId();
        Long secondUserId = secondUser.getId();
        List<User> friendsFirstUserBefore = userService.getAllFriendsByUserId(firstUserId);
        List<User> friendsSecondUserBefore = userService.getAllFriendsByUserId(secondUserId);
        assertTrue(friendsFirstUserBefore.isEmpty());
        assertTrue(friendsSecondUserBefore.isEmpty());

        //when
        userService.addFriend(firstUserId, secondUserId);

        //then
        List<User> friendsFirstUserAfter = userService.getAllFriendsByUserId(firstUserId);
        List<User> friendsSecondUserAfter = userService.getAllFriendsByUserId(secondUserId);
        User friendUser = friendsFirstUserAfter.get(0);
        Long friendUserId = friendUser.getId();
        assertEquals(secondUserId, friendUserId);
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
        List<User> friendsFirstUserBefore = userService.getAllFriendsByUserId(idFirstUser);
        List<User> friendsSecondUserBefore = userService.getAllFriendsByUserId(idSecondUser);
        User friend = friendsFirstUserBefore.get(0);
        Long idFriend = friend.getId();
        assertEquals(idSecondUser, idFriend);
        assertTrue(friendsSecondUserBefore.isEmpty());

        //when
        userService.removeFriend(idFirstUser, idSecondUser);

        //then
        List<User> friendsFirstUserAfter = userService.getAllFriendsByUserId(idFirstUser);
        List<User> friendsSecondUserAfter = userService.getAllFriendsByUserId(idSecondUser);
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
        int size = friends.size();
        User userFriends = friends.get(0);
        assertEquals(1, size);
        assertEquals(secondUser, userFriends);
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
        int size = mutualFriendsAfter.size();
        User friend = mutualFriendsAfter.get(0);
        Long friendId = friend.getId();
        assertEquals(1, size);
        assertEquals(idMutual, friendId);
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