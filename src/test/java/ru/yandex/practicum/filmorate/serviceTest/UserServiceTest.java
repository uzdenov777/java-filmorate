//package ru.yandex.practicum.filmorate.serviceTest;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.web.server.ResponseStatusException;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.service.UserService;
//import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryUserStorage;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class UserServiceTest {
//    UserService userService;
//    User firstUser;
//    User secondUser;
//
//    @BeforeEach
//    void setUp() {
//        userService = new UserService(new InMemoryUserStorage());
//
//        firstUser = new User();
//        firstUser.setName("John Doe");
//        firstUser.setLogin("login");
//        firstUser.setBirthday(LocalDate.now());
//        firstUser.setEmail("adsdas009@gmail.com");
//
//        secondUser = new User();
//        secondUser.setName("Dani");
//        secondUser.setLogin("777");
//        secondUser.setBirthday(LocalDate.now());
//        secondUser.setEmail("google@icloud.com");
//    }
//
//    @Test
//    @DisplayName("Должен успешно добавить user-а")
//    void add_addUser() {
//        //given
//        List<User> before = userService.getAllUsers();
//        assertTrue(before.isEmpty());
//
//        //when
//        userService.add(firstUser);
//
//        //then
//        List<User> users = userService.getAllUsers();
//        assertEquals(1, users.size());
//        assertEquals(firstUser, users.get(0));
//    }
//
//    @Test
//    @DisplayName("Должен успешно обновить user, когда user для обновления был ранее добавлен")
//    void update_existingUserToUpdate() {
//        //given
//        userService.add(firstUser);
//        List<User> before = userService.getAllUsers();
//        assertEquals(1, before.size());
//        assertEquals(firstUser, before.get(0));
//
//        //when
//        User newUser = new User();
//        newUser.setId(firstUser.getId());
//        newUser.setName("newUser");
//        newUser.setLogin("login newUser");
//        newUser.setBirthday(LocalDate.now().minusDays(1));
//        newUser.setEmail("adsdas009@gmail.com");
//        userService.update(newUser);
//
//        //then
//        List<User> userList = userService.getAllUsers();
//        assertEquals(1, userList.size());
//        assertEquals(newUser, userList.get(0));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда user-а для обновления с таким ID нету")
//    void update_notUpdatedUser_noExistingUserToUpdate() {
//        //when+then
//        firstUser.setId(1L);
//        assertThrows(ResponseStatusException.class, () -> userService.update(firstUser));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список всех добавленных фильмов, когда фильмы добавлены")
//    void getAllUsers_getNotEmptyListAddUsers() {
//        //given
//        List<User> before = userService.getAllUsers();
//        assertTrue(before.isEmpty());
//
//        //when
//        User newUser = new User();
//        newUser.setName("newUser");
//        newUser.setLogin("login newUser");
//        newUser.setBirthday(LocalDate.now().minusDays(1));
//        newUser.setEmail("30Сантиметров@.gmail.com");
//        userService.add(firstUser);
//        userService.add(newUser);
//
//        //then
//        List<User> userList = userService.getAllUsers();
//        assertEquals(2, userList.size());
//        assertEquals(firstUser, userList.get(0));
//        assertEquals(newUser, userList.get(1));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустой список всех user-ов, когда user-ы не добавлены")
//    void getAllUsers_getEmptyListAddUsers() {
//        List<User> userList = userService.getAllUsers();
//
//        assertTrue(userList.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должны успешно добавиться друг к другу в друзья, когда оба пользователя сущ и не равны")
//    void addFriend_usersExistAndNotEqual() {
//        //given
//        userService.add(firstUser);
//        userService.add(secondUser);
//        Set<Long> friendsFirstUserBefore = firstUser.getFriends();
//        Set<Long> friendsSecondUserBefore = secondUser.getFriends();
//        assertTrue(friendsFirstUserBefore.isEmpty());
//        assertTrue(friendsSecondUserBefore.isEmpty());
//
//        //when
//        long idFirstUser = firstUser.getId();
//        long idSecondUser = secondUser.getId();
//        userService.addFriend(idFirstUser, idSecondUser);
//
//        //then
//        Set<Long> friendsFirstUserAfter = firstUser.getFriends();
//        Set<Long> friendsSecondUserAfter = secondUser.getFriends();
//        assertTrue(friendsFirstUserAfter.contains(idSecondUser));
//        assertTrue(friendsSecondUserAfter.contains(idFirstUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда первый пользователь не добавлен")
//    void addFriend_throwResponseStatusException_notExistFirstUser() {
//        //given
//        firstUser.setId(155L); //нужно вручную установить ID, обычно он уст при добавлении
//        userService.add(secondUser);
//        Set<Long> friendsFirstUserBefore = firstUser.getFriends();
//        Set<Long> friendsSecondUserBefore = secondUser.getFriends();
//        assertTrue(friendsFirstUserBefore.isEmpty());
//        assertTrue(friendsSecondUserBefore.isEmpty());
//
//        //when+then
//        long idFirstUser = firstUser.getId();
//        long idSecondUser = secondUser.getId();
//        assertThrows(ResponseStatusException.class, () -> userService.addFriend(idFirstUser, idSecondUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда второй пользователь не добавлен")
//    void addFriend_throwResponseStatusException_notExistSecondUser() {
//        //given
//        secondUser.setId(155L); //нужно вручную установить ID, обычно он уст при добавлении
//        userService.add(firstUser);
//        Set<Long> friendsFirstUserBefore = firstUser.getFriends();
//        Set<Long> friendsSecondUserBefore = secondUser.getFriends();
//        assertTrue(friendsFirstUserBefore.isEmpty());
//        assertTrue(friendsSecondUserBefore.isEmpty());
//
//        //when+then
//        long idFirstUser = firstUser.getId();
//        long idSecondUser = secondUser.getId();
//        assertThrows(ResponseStatusException.class, () -> userService.addFriend(idFirstUser, idSecondUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда первый пользователь не добавлен")
//    void addFriend_throwResponseStatusException_userIsSelf() {
//        //given
//        userService.add(firstUser);
//        Set<Long> friendsFirstUserBefore = firstUser.getFriends();
//        assertTrue(friendsFirstUserBefore.isEmpty());
//
//        //when+then
//        long idFirstUser = firstUser.getId();
//        assertThrows(ResponseStatusException.class, () -> userService.addFriend(idFirstUser, idFirstUser));
//    }
//
//    @Test
//    @DisplayName("Должны успешно удалить друг к друга из друзей, когда оба пользователя сущ и не равны")
//    void deleteFriend_usersExistAndNotEqual() {
//        //given
//        userService.add(firstUser);
//        userService.add(secondUser);
//        long idFirstUser = firstUser.getId();
//        long idSecondUser = secondUser.getId();
//        userService.addFriend(idFirstUser, idSecondUser);
//        Set<Long> friendsFirstUserBefore = firstUser.getFriends();
//        Set<Long> friendsSecondUserBefore = secondUser.getFriends();
//        assertTrue(friendsFirstUserBefore.contains(idSecondUser));
//        assertTrue(friendsSecondUserBefore.contains(idFirstUser));
//
//        //when
//        userService.deleteFriend(idFirstUser, idSecondUser);
//
//        //then
//        Set<Long> friendsFirstUserAfter = firstUser.getFriends();
//        Set<Long> friendsSecondUserAfter = secondUser.getFriends();
//        assertTrue(friendsFirstUserAfter.isEmpty());
//        assertTrue(friendsSecondUserAfter.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда первый пользователь не добавлен")
//    void deleteFriend_throwResponseStatusException_notExistFirstUser() {
//        //given
//        firstUser.setId(155L); //нужно вручную установить ID, обычно он уст при добавлении
//        userService.add(secondUser);
//        long idFirstUser = firstUser.getId();
//        long idSecondUser = secondUser.getId();
//        Set<Long> friendsSecondUser = secondUser.getFriends();
//        assertFalse(friendsSecondUser.contains(idFirstUser));
//
//        //when+then
//        assertThrows(ResponseStatusException.class, () -> userService.deleteFriend(idFirstUser, idSecondUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда второй пользователь не добавлен")
//    void deleteFriend_throwResponseStatusException_notExistSecondUser() {
//        //given
//        userService.add(firstUser);
//
//        //when+then
//        long idNotExistUser = 10;
//        long idFirstUser = firstUser.getId();
//        assertThrows(ResponseStatusException.class, () -> userService.deleteFriend(idFirstUser, idNotExistUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда первый пользователь не добавлен")
//    void deleteFriend_throwResponseStatusException_userIsSelf() {
//        //given
//        userService.add(firstUser);
//
//        //when+then
//        long idFirstUser = firstUser.getId();
//        assertThrows(ResponseStatusException.class, () -> userService.deleteFriend(idFirstUser, idFirstUser));
//    }
//
//    @Test
//    @DisplayName("Должен вернут список друзей, которые добавлены, когда пользователь тоже добавлен")
//    void getFriends_returnNotEmptyList_AddingExistFriendAndUserExist() {
//        //given
//        userService.add(firstUser);
//        userService.add(secondUser);
//        long idFirst = firstUser.getId();
//        long idSecond = secondUser.getId();
//        userService.addFriend(idFirst, idSecond);
//
//        //when
//        List<User> friends = userService.getFriends(idFirst);
//
//        //then
//        assertEquals(1, friends.size());
//        assertEquals(secondUser, friends.get(0));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустой список друзей пользователя, когда друзья еще не добавлены, а пользователь существует")
//    void getFriends_returnEmptyList_notAddingFriendAndUserExist() {
//        //given
//        userService.add(firstUser);
//        long idFirst = firstUser.getId();
//
//        //when
//        List<User> friends = userService.getFriends(idFirst);
//
//        //then
//        assertTrue(friends.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда пользователь не найден")
//    void getFriends_throwResponseStatusException_userNotExist() {
//        //when+then
//        long notExistIdUser = 56;
//        assertThrows(ResponseStatusException.class, () -> userService.getFriends(notExistIdUser));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть только существующих друзей, когда у некоторых id нет пользователя")
//    void getFriends_returnOnlyExistingFriends_someFriendIdsDoNotExist() {
//        //given
//        userService.add(firstUser);
//        userService.add(secondUser);
//
//        long idFirst = firstUser.getId();
//        long idSecond = secondUser.getId();
//
//        userService.addFriend(idFirst, idSecond);
//        Set<Long> friendsFirstUserBefore = firstUser.getFriends();
//        assertTrue(friendsFirstUserBefore.contains(idSecond));
//        assertEquals(1, friendsFirstUserBefore.size());
//
//        //when
//        friendsFirstUserBefore.add(789L); //добавляем вручную не существующий id в друзья пользователя
//        List<User> friendsFirstUserAfter = userService.getFriends(idFirst);
//
//        //then
//        assertTrue(friendsFirstUserAfter.contains(secondUser));
//        assertEquals(1, friendsFirstUserAfter.size());
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустой лист, когда не существует друзей по этим id")
//    void getFriends_returnEmptyList_allFriendsNotExist() {
//        //given
//        userService.add(firstUser);
//        long idFirst = firstUser.getId();
//        Set<Long> friendsFirstUserBefore = firstUser.getFriends();
//        assertTrue(friendsFirstUserBefore.isEmpty());
//
//        //when
//        friendsFirstUserBefore.add(789L);
//        friendsFirstUserBefore.add(546L); //добавляем вручную не существующий id в друзья пользователя
//        List<User> friendsFirstUserAfter = userService.getFriends(idFirst);
//
//        //then
//        assertTrue(friendsFirstUserAfter.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен вернуть не пустой список общих друзей, когда они есть")
//    void getListMutualFriends_returnNotEmptyList_existMutualFriends() {
//        //given
//        User mutualFriend = new User();
//
//        userService.add(firstUser);
//        userService.add(secondUser);
//        userService.add(mutualFriend);
//
//        long idFirst = firstUser.getId();
//        long idSecond = secondUser.getId();
//        List<User> mutualFriendsBefore = userService.getListMutualFriends(idFirst, idSecond);
//        assertTrue(mutualFriendsBefore.isEmpty());
//
//        //when
//        long idMutual = mutualFriend.getId();
//        userService.addFriend(idFirst, idMutual);
//        userService.addFriend(idSecond, idMutual);
//        List<User> mutualFriendsAfter = userService.getListMutualFriends(idFirst, idSecond);
//
//        //then
//        assertEquals(1, mutualFriendsAfter.size());
//        assertTrue(mutualFriendsAfter.contains(mutualFriend));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустой список общих друзей, когда нет общих друзей")
//    void getListMutualFriends_returnEmptyList_notExistMutualFriends() {
//        //given
//        User friendDinis = new User();
//        User friendAdam = new User();
//
//        userService.add(firstUser);
//        userService.add(secondUser);
//        userService.add(friendDinis);
//        userService.add(friendAdam);
//
//        long idFirst = firstUser.getId();
//        long idSecond = secondUser.getId();
//        List<User> mutualFriendsBefore = userService.getListMutualFriends(idFirst, idSecond);
//        assertTrue(mutualFriendsBefore.isEmpty());
//
//        //when
//        long idFriendDinis = friendDinis.getId();
//        long idFriendAdam = friendAdam.getId();
//        userService.addFriend(idFirst, idFriendDinis);
//        userService.addFriend(idSecond, idFriendAdam);
//        List<User> mutualFriendsAfter = userService.getListMutualFriends(idFirst, idSecond);
//
//        //then
//        assertTrue(mutualFriendsAfter.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда первый пользователь не существует")
//    void getListMutualFriends_throwResponseStatusException_firstUserNotExist() {
//        //given
//        userService.add(secondUser);
//
//        //when+then
//        long idNotExistFirstUser = 456456;
//        long idSecond = secondUser.getId();
//        assertThrows(ResponseStatusException.class, () -> userService.getListMutualFriends(idNotExistFirstUser, idSecond));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда второй пользователь не существует")
//    void getListMutualFriends_throwResponseStatusException_secondUserNotExist() {
//        //given
//        userService.add(firstUser);
//
//        //when+then
//        long idNotExistSecondUser = 456456;
//        long idFirst = firstUser.getId();
//        assertThrows(ResponseStatusException.class, () -> userService.getListMutualFriends(idFirst, idNotExistSecondUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда пользователь ищет общих друзей у самого себя")
//    void getListMutualFriends_throwResponseStatusException_usersAreSam() {
//        //given
//        userService.add(firstUser);
//
//        //when+then
//        long idFirst = firstUser.getId();
//        assertThrows(ResponseStatusException.class, () -> userService.getListMutualFriends(idFirst, idFirst));
//    }
//}