//package ru.yandex.practicum.filmorate.controllerTest;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.web.server.ResponseStatusException;
//import ru.yandex.practicum.filmorate.controller.UserController;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class UserControllerTest {
//
//    UserController userController;
//    User userFirst;
//
//    @BeforeEach
//    void setUp() {
//        userController = new UserController();
//        userFirst = new User();
//        userFirst.setName("John Doe");
//        userFirst.setLogin("login");
//        userFirst.setBirthday(LocalDate.now());
//        userFirst.setEmail("adsdas009@gmail.com");
//    }
//
//
//    @Test
//    @DisplayName("Должен успешно добавить user-а")
//    void add_addUser() {
//        //given
//        List<User> before = userController.getAllUsers();
//        assertTrue(before.isEmpty());
//
//        //when
//        userController.add(userFirst);
//
//        //then
//        List<User> users = userController.getAllUsers();
//        assertEquals(1, users.size());
//        assertEquals(userFirst, users.get(0));
//    }
//
//    @Test
//    @DisplayName("Должен успешно обновить user, когда user для обновления был ранее добавлен")
//    void update_existingUserToUpdate() {
//        //given
//        userController.add(userFirst);
//        List<User> before = userController.getAllUsers();
//        assertEquals(1, before.size());
//        assertEquals(userFirst, before.get(0));
//
//        //when
//        User newUser = new User();
//        newUser.setId(userFirst.getId());
//        newUser.setName("newUser");
//        newUser.setLogin("login newUser");
//        newUser.setBirthday(LocalDate.now().minusDays(1));
//        newUser.setEmail("adsdas009@gmail.com");
//        userController.update(newUser);
//
//        //then
//        List<User> userList = userController.getAllUsers();
//        assertEquals(1, userList.size());
//        assertEquals(newUser, userList.get(0));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда user-а для обновления с таким ID нету")
//    void update_notUpdatedUser_noExistingUserToUpdate() {
//        //when+then
//        assertThrows(ResponseStatusException.class, () -> userController.update(userFirst));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список всех добавленных фильмов, когда фильмы добавлены")
//    void getAllUsers_getNotEmptyListAddUsers() {
//        //given
//        List<User> before = userController.getAllUsers();
//        assertTrue(before.isEmpty());
//
//        //when
//        User newUser = new User();
//        newUser.setName("newUser");
//        newUser.setLogin("login newUser");
//        newUser.setBirthday(LocalDate.now().minusDays(1));
//        newUser.setEmail("30Сантиметров@.gmail.com");
//        userController.add(userFirst);
//        userController.add(newUser);
//
//        //then
//        List<User> userList = userController.getAllUsers();
//        assertEquals(2, userList.size());
//        assertEquals(userFirst, userList.get(0));
//        assertEquals(newUser, userList.get(1));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустой список всех user-ов, когда user-ы не добавлены")
//    void getAllUsers_getEmptyListAddUsers() {
//        List<User> userList = userController.getAllUsers();
//
//        assertTrue(userList.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен устанавливать новый ID для пользователя при каждом добавлении")
//    void getFilmId() {
//        //given
//        User newUser = new User();
//        newUser.setName("John Doe");
//        newUser.setLogin("login");
//        newUser.setBirthday(LocalDate.now());
//        newUser.setEmail("adsdas009@gmail.com");
//        assertEquals(0, userFirst.getId());
//        assertEquals(0, newUser.getId());
//
//        //when
//        userController.add(userFirst);
//        userController.add(newUser);
//
//        //then
//        int idUser = userFirst.getId();
//        int idNewUser = newUser.getId();
//        assertEquals(1, idUser);
//        assertEquals(2, idNewUser);
//    }
//}