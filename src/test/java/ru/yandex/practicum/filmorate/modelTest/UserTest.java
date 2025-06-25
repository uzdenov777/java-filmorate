package ru.yandex.practicum.filmorate.modelTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    private User testUser;
    private String login;

    @BeforeEach
    void setUp() {
        testUser = new User();
        login = "testLogin";
    }

    @DisplayName("Должен сохранить в поле name пользователя ,то что указанно в поле login(оно обязательное), когда передаём пустую строку для сохранения в поле name")
    @Test
    void setDisplayName_whenNameEmpty() {
        //given
        testUser.setLogin(login);
        String nameBefore = testUser.getName();
        assertNull(nameBefore);

        //when
        testUser.setDisplayName("", testUser.getLogin());

        //then
        String nameAfter = testUser.getName();
        assertEquals(login, nameAfter);
    }

    @DisplayName("Должен сохранить в поле name пользователя ,то что указанно в поле login(оно обязательное), когда передаём name == NULL для сохранения в поле name")
    @Test
    void setDisplayName_WhenNameNull() {
        //given
        testUser.setLogin(login);
        String nameBefore = testUser.getName();
        assertNull(nameBefore);

        //when
        testUser.setDisplayName(null, testUser.getLogin());

        //then
        String nameAfter = testUser.getName();
        assertEquals(login, nameAfter);
    }

    @DisplayName("Должен сохранить в поле name пользователя ,то что указанно в setNameTest, когда передаём name, который не пустой и не NULL")
    @Test
    void setDisplayName_WhenNameNotEmptyAndNull() {
        //given
        testUser.setLogin(login);
        String nameBefore = testUser.getName();
        assertNull(nameBefore);

        //when
        String setNameTest = "testName";
        testUser.setDisplayName(setNameTest, testUser.getLogin());

        //then
        String nameAfter = testUser.getName();
        assertEquals(setNameTest, nameAfter);
    }
}
