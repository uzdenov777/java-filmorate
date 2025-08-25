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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class FilmLikesDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private FilmLikesDbStorage filmLikesDbStorage;
    private FilmsDbStorage filmsDbStorage;
    private UserDbStorage userDbStorage;

    private Film firstFilm;
    private User userFilmorate;

    @BeforeEach
    void setUp() {
        filmLikesDbStorage = new FilmLikesDbStorage(jdbcTemplate);
        filmsDbStorage = new FilmsDbStorage(jdbcTemplate);
        userDbStorage = new UserDbStorage(jdbcTemplate);

        firstFilm = new Film();
        firstFilm.setName("FirstFilm");
        firstFilm.setDescription("FirstFilmDescription");
        firstFilm.setDuration(10L);
        firstFilm.setReleaseDate(LocalDate.now());
        firstFilm.setMpa(new Mpa(1, "G"));

        userFilmorate = new User();
        userFilmorate.setName("UserFilmorate");
        userFilmorate.setEmail("email@.com");
        userFilmorate.setLogin("loginUserFilmorate");
        userFilmorate.setBirthday(LocalDate.now());
    }

    @Test
    @DisplayName("Должен добавить фильму лайк, когда и фильм и пользователь существуют")
    void addLikeFilm_addingLikeFilm_UserAndFilmExist() {
        //given
        filmsDbStorage.add(firstFilm);
        userDbStorage.add(userFilmorate);
        List<Long> likesFilmByFirstFilmBefore = filmLikesDbStorage.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmBefore.isEmpty());

        //when
        filmLikesDbStorage.addLikeFilm(firstFilm.getId(), userFilmorate.getId());

        //then
        List<Long> likesFilmByFirstFilmAfter = filmLikesDbStorage.getFilmLikesByFilmId(firstFilm.getId());
        assertEquals(userFilmorate.getId(), likesFilmByFirstFilmAfter.get(0));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении лайка, когда фильм не существует")
    void addLikeFilm_notAddingLikeFilm_filmNotExist() {
        //given
        userDbStorage.add(userFilmorate);

        //when+then
        long filmIdNotExist = 777L;
        assertThrows(DataIntegrityViolationException.class, () -> filmLikesDbStorage.addLikeFilm(filmIdNotExist, userFilmorate.getId()));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении лайка, когда пользователь не существует")
    void addLikeFilm_notAddingLikeFilm_userNotExist() {
        //given
        filmsDbStorage.add(firstFilm);

        //when+then
        long userIdNotExist = 777L;
        assertThrows(DataIntegrityViolationException.class, () -> filmLikesDbStorage.addLikeFilm(firstFilm.getId(), userIdNotExist));
    }

    @Test
    @DisplayName("Должен удалить фильму лайк, когда и фильм и пользователь существуют и добавлен лайк")
    void removeLikeFilm_removeLikeFilm_UserAndFilmExist() {
        //given
        filmsDbStorage.add(firstFilm);
        userDbStorage.add(userFilmorate);
        filmLikesDbStorage.addLikeFilm(firstFilm.getId(), userFilmorate.getId());
        List<Long> likesFilmByFirstFilmBefore = filmLikesDbStorage.getFilmLikesByFilmId(firstFilm.getId());
        assertEquals(userFilmorate.getId(), likesFilmByFirstFilmBefore.get(0));

        //when
        filmLikesDbStorage.removeLikeFilm(firstFilm.getId(), userFilmorate.getId());

        //then
        List<Long> likesFilmByFirstFilmAfter = filmLikesDbStorage.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен удалить все лайки фильму, когда есть лайки")
    void deleteAllFilmLikesByFilmId_removeAllLikesFilmById_UserAndFilmExist() {
        //given
        filmsDbStorage.add(firstFilm);
        userDbStorage.add(userFilmorate);
        filmLikesDbStorage.addLikeFilm(firstFilm.getId(), userFilmorate.getId());
        List<Long> likesFilmByFirstFilmBefore = filmLikesDbStorage.getFilmLikesByFilmId(firstFilm.getId());
        assertEquals(userFilmorate.getId(), likesFilmByFirstFilmBefore.get(0));

        //when
        filmLikesDbStorage.deleteAllFilmLikesByFilmId(firstFilm.getId());

        //then
        List<Long> likesFilmByFirstFilmAfter = filmLikesDbStorage.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех лайков фильму, когда нет ни одного лайка")
    void getFilmLikesByFilmId_returnEmptyListAllLikesFilmById_notExistLikes() {
        //given
        filmsDbStorage.add(firstFilm);

        //when
        List<Long> allLikesFirstFilm = filmLikesDbStorage.getFilmLikesByFilmId(firstFilm.getId());

        //then
        assertTrue(allLikesFirstFilm.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть не пустой список всех лайков фильму, когда лайки поставлены")
    void getFilmLikesByFilmId_returnNotEmptyListAllLikesFilmById_ExistLikes() {
        //given
        filmsDbStorage.add(firstFilm);
        userDbStorage.add(userFilmorate);
        filmLikesDbStorage.addLikeFilm(firstFilm.getId(), userFilmorate.getId());

        //when
        List<Long> allLikesFirstFilm = filmLikesDbStorage.getFilmLikesByFilmId(firstFilm.getId());

        //then
        assertEquals(userFilmorate.getId(), allLikesFirstFilm.get(0));
    }
}