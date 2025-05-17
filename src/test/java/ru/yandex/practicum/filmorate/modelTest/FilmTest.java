package ru.yandex.practicum.filmorate.modelTest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {

    private Validator validator;
    private Film film;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        film = new Film();
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с name, когда пустой name")
    public void setEmptyName() {
        film.setDescription("This is a test");
        film.setId(1);
        film.setDuration(20L);
        film.setReleaseDate(LocalDate.now());

        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с name, когда name null")
    public void setNameNull() {
        film.setId(1);
        film.setDescription("This is a test");
        film.setDuration(20L);
        film.setReleaseDate(LocalDate.now());

        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Set-violations будет пустой, когда name есть и не null")
    public void setValidName() {
        film.setId(1);
        film.setDescription("This is a test");
        film.setDuration(20L);
        film.setReleaseDate(LocalDate.now());

        film.setName("FilmTest");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с description, когда description null")
    public void setDescriptionNull() {
        film.setId(1);
        film.setName("FilmTest");
        film.setDuration(20L);
        film.setReleaseDate(LocalDate.now());

        film.setDescription(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с description, когда description пустой")
    public void setDescriptionEmpty() {
        film.setId(1);
        film.setName("FilmTest");
        film.setDuration(20L);
        film.setReleaseDate(LocalDate.now());

        film.setDescription("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с description, когда description превышает лимит 200 символов description")
    public void setDescriptionExceedsLimitSymbol() {
        film.setId(1);
        film.setName("FilmTest");
        film.setDuration(20L);
        film.setReleaseDate(LocalDate.now());

        String description201Symbol = "12345678910111213141516171819202122232425262728293031323334353637383940414243444" +
                "5464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899100101102103";
        film.setDescription(description201Symbol);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Set-violations будет пустой, когда description есть, не null и не превышает лимит в 200 символов")
    public void setValiDescription() {
        film.setId(1);
        film.setName("FilmTest");
        film.setDuration(20L);
        film.setReleaseDate(LocalDate.now());

        film.setDescription("This is a test");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с releaseDate, когда releaseDate null")
    public void setReleaseDateNull() {
        film.setId(1);
        film.setName("FilmTest");
        film.setDuration(20L);
        film.setDescription("This is a test");

        film.setReleaseDate(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Выбросит исключение ResponseStatusException, когда releaseDate раньше или ровно 1895-12-28")
    public void setReleaseDateEarlier1895_12_28() {
        film.setId(1);
        film.setName("FilmTest");
        film.setDuration(20L);
        film.setDescription("This is a test");

        LocalDate releaseDateEarlier1895_12_28 = LocalDate.of(1895, 12, 28);
        film.setReleaseDate(releaseDateEarlier1895_12_28);
        assertThrows(ResponseStatusException.class, () -> Film.isValidReleaseDate(film));

        LocalDate releaseDateBefore1895_12_28 = LocalDate.of(1895, 12, 27);
        film.setReleaseDate(releaseDateBefore1895_12_28);
        assertThrows(ResponseStatusException.class, () -> Film.isValidReleaseDate(film));
    }

    @Test
    @DisplayName("Set-violations будет пустой и не выбросит никаких исключений,  когда releaseDate позже 1895-12-28 и не null")
    public void setValidReleaseDate() {
        film.setId(1);
        film.setName("FilmTest");
        film.setDuration(20L);
        film.setDescription("This is a test");

        film.setReleaseDate(LocalDate.now());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с duration, когда duration 0")
    public void setValidDuration0() {
        film.setId(1);
        film.setName("FilmTest");
        film.setDescription("This is a test");
        film.setReleaseDate(LocalDate.now());

        film.setDuration(0L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Вернет в Set-violations неверную валидацию с duration, когда duration null")
    public void setValidDurationNull() {
        film.setId(1);
        film.setName("FilmTest");
        film.setDescription("This is a test");
        film.setReleaseDate(LocalDate.now());

        film.setDuration(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        for (ConstraintViolation<Film> v : violations) {
            System.out.println(v.getPropertyPath() + " - " + v.getMessage());
        }

        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Set-violations будет пустой, когда duration не null и не меньше 1")
    public void setValidDuration() {
        film.setId(1);
        film.setName("FilmTest");
        film.setDescription("This is a test");
        film.setReleaseDate(LocalDate.now());

        film.setDuration(1L);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }
}
