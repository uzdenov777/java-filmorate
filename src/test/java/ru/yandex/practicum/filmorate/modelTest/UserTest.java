package ru.yandex.practicum.filmorate.modelTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    private Validator validator;
    private User testUser;

    @BeforeEach
    protected void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        testUser = new User();
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с Email, когда неверный формат Email")
    public void setNotValidEmail() {
        testUser.setId(1);
        testUser.setLogin("test");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        testUser.setDisplayName(testUser.getName(), testUser.getLogin());

        testUser.setEmail("failEmail@@@/com");
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        for (ConstraintViolation<User> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с Email, когда пустой Email")
    public void setEmptyEmail() {
        testUser.setId(1);
        testUser.setLogin("test");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        testUser.setDisplayName(testUser.getName(), testUser.getLogin());

        testUser.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        for (ConstraintViolation<User> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Set-violations будет пустой, когда Email есть и валидного формата")
    public void setValidEmail() {
        testUser.setId(1);
        testUser.setLogin("test");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        testUser.setDisplayName(testUser.getName(), testUser.getLogin());

        testUser.setEmail("uzdenov02@bk.ru");
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с login, когда login null")
    public void setNullLogin() {
        testUser.setId(1);
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        testUser.setEmail("uzdenov02@bk.ru");

        testUser.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        for (ConstraintViolation<User> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с login, когда login пустой")
    public void setEmptyLogin() {
        testUser.setId(1);
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        testUser.setEmail("uzdenov02@bk.ru");

        testUser.setLogin(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        for (ConstraintViolation<User> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Set-violations будет пустой, когда login есть и не пустой")
    public void setValidLogin() {
        testUser.setId(1);
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        testUser.setEmail("uzdenov02@bk.ru");

        testUser.setLogin("login");
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с birthday, когда birthday null")
    public void setNullBirthday() {
        testUser.setId(1);
        testUser.setEmail("uzdenov02@bk.ru");
        testUser.setLogin("login");

        testUser.setBirthday(null);
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        for (ConstraintViolation<User> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с birthday, когда birthday в будущем")
    public void setBirthdayInFuture() {
        testUser.setId(1);
        testUser.setEmail("uzdenov02@bk.ru");
        testUser.setLogin("login");

        LocalDate birthdayInFuture = LocalDate.now().plusDays(1);
        testUser.setBirthday(birthdayInFuture);
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        for (ConstraintViolation<User> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Set-violations будет пустой, когда birthday есть и не в будущем")
    public void setValidBirthday() {
        testUser.setId(1);
        testUser.setEmail("uzdenov02@bk.ru");
        testUser.setLogin("login");

        testUser.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);

        assertTrue(violations.isEmpty());
    }
}
