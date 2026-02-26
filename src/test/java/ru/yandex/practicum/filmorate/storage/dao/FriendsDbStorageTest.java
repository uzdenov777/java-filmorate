//package ru.yandex.practicum.filmorate.storage.dao;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.jdbc.Sql;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@JdbcTest
//class FriendsDbStorageTest {
//    private final JdbcTemplate jdbcTemplate;
//
//    private FriendsDbStorage friendsDbStorage;
//    private UserDbStorage userDbStorage;
//
//    private User firstUser;
//    private User secondUser;
//
//    @BeforeEach
//    void setup() {
//        friendsDbStorage = new FriendsDbStorage(jdbcTemplate);
//        userDbStorage = new UserDbStorage(jdbcTemplate);
//
//        firstUser = new User();
//        firstUser.setName("first");
//        firstUser.setEmail("first@email");
//        firstUser.setLogin("firstLogin");
//        firstUser.setBirthday(LocalDate.now());
//
//        secondUser = new User();
//        secondUser.setName("second");
//        secondUser.setEmail("second@email");
//        secondUser.setLogin("secondLogin");
//        secondUser.setBirthday(LocalDate.now());
//    }
//
//    @Test
//    @DisplayName("Должен добавить друга secondUser для firstUser")
//    void addFriend_addingFriends_bothUsersExist() { //проверка односторонней дружбы
//        //given
//        userDbStorage.add(firstUser);
//        userDbStorage.add(secondUser);
//        Long firstUserId = firstUser.getId();
//        Long secondUserId = secondUser.getId();
//        List<User> allUsers = userDbStorage.findAll();
//        List<Long> friendIdFirstUserBefore = friendsDbStorage.getFriendsIdByUserId(firstUserId);
//        List<Long> friendsIdSecondUserBefore = friendsDbStorage.getFriendsIdByUserId(secondUserId);
//        assertEquals(2, allUsers.size());
//        assertTrue(friendIdFirstUserBefore.isEmpty());
//        assertTrue(friendsIdSecondUserBefore.isEmpty());
//
//        //when
//        friendsDbStorage.addFriend(firstUserId, secondUser.getId());
//
//        //then
//        List<Long> friendIdFirstUserAfter = friendsDbStorage.getFriendsIdByUserId(firstUserId);
//        List<Long> friendsIdSecondUserAfter = friendsDbStorage.getFriendsIdByUserId(secondUserId);
//        assertEquals(secondUserId, friendIdFirstUserAfter.get(0));
//        assertTrue(friendsIdSecondUserAfter.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда нового друга нет в БД")
//    void addFriend_notAddingFriends_secondUserNotExist() { //проверка односторонней дружбы
//        //given
//        userDbStorage.add(firstUser);
//        List<User> allUsers = userDbStorage.findAll();
//        int size = allUsers.size();
//        assertEquals(1, size);
//
//        //when+then
//        Long idUserNotExist = 777L;
//        Long firstUserId = firstUser.getId();
//        assertThrows(DataIntegrityViolationException.class, () -> friendsDbStorage.addFriend(firstUserId, idUserNotExist));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда пользователя, которому добавляем друга нет в БД")
//    void addFriend_notAddingFriends_firstUserNotExist() { //проверка односторонней дружбы
//        //given
//        userDbStorage.add(secondUser);
//        List<User> allUsers = userDbStorage.findAll();
//        int size = allUsers.size();
//        assertEquals(1, size);
//
//        //when+then
//        Long idUserNotExist = 777L;
//        Long secondUserId = secondUser.getId();
//        assertThrows(DataIntegrityViolationException.class, () -> friendsDbStorage.addFriend(idUserNotExist, secondUserId));
//    }
//
//    @Test
//    @DisplayName("Должен удалить друга secondUser для firstUser")
//    void removeFriend_removingFriend_bothUsersExist() { //проверка односторонней дружбы
//        //given
//        userDbStorage.add(firstUser);
//        userDbStorage.add(secondUser);
//        Long firstUserId = firstUser.getId();
//        Long secondUserId = secondUser.getId();
//        friendsDbStorage.addFriend(firstUserId, secondUserId);
//        List<Long> friendIdFirstUserBefore = friendsDbStorage.getFriendsIdByUserId(firstUserId);
//        List<Long> friendsIdSecondUserBefore = friendsDbStorage.getFriendsIdByUserId(secondUserId);
//        assertEquals(secondUserId, friendIdFirstUserBefore.get(0));
//        assertTrue(friendsIdSecondUserBefore.isEmpty());
//
//        //when
//        friendsDbStorage.removeFriend(firstUserId, secondUserId);
//
//        //then
//        List<Long> friendIdFirstUserAfter = friendsDbStorage.getFriendsIdByUserId(firstUserId);
//        List<Long> friendsIdSecondUserAfter = friendsDbStorage.getFriendsIdByUserId(secondUserId);
//        assertTrue(friendIdFirstUserAfter.isEmpty());
//        assertTrue(friendsIdSecondUserAfter.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда друга для удаления нет в БД")
//    void removeFriend_removingFriend_secondUserNotExist() { //проверка односторонней дружбы
//        //given
//        userDbStorage.add(firstUser);
//        List<User> allUsers = userDbStorage.findAll();
//        int size = allUsers.size();
//        assertEquals(1, size);
//
//        //when+then
//        Long idUserNotExist = 777L;
//        Long firstUserId = firstUser.getId();
//        assertThrows(DataIntegrityViolationException.class, () -> friendsDbStorage.addFriend(firstUserId, idUserNotExist));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список ID друзей, когда есть друзья у пользователя")
//    void getFriendsIdByUserId_friendsAdded() {
//        //given
//        userDbStorage.add(firstUser);
//        userDbStorage.add(secondUser);
//        friendsDbStorage.addFriend(firstUser.getId(), secondUser.getId());
//
//        //when
//        Long firstUserId = firstUser.getId();
//        List<Long> friendIdFirstUserBefore = friendsDbStorage.getFriendsIdByUserId(firstUserId);
//
//        //then
//        Long secondUserId = secondUser.getId();
//        Long friendsId = friendIdFirstUserBefore.get(0);
//        assertEquals(secondUserId, friendsId);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустой список ID друзей, когда у пользователя нет друзей")
//    void getFriendsIdByUserId_friendsNotAdded() {
//        //given
//        userDbStorage.add(firstUser);
//
//        //when
//        Long firstUserId = firstUser.getId();
//        List<Long> friendIdFirstUserBefore = friendsDbStorage.getFriendsIdByUserId(firstUserId);
//
//        //then
//        assertTrue(friendIdFirstUserBefore.isEmpty());
//    }
//}