package ru.yandex.practicum.filmorate.service;

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
import ru.yandex.practicum.filmorate.storage.dao.FilmLikesDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmsDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class FilmLikesServiceTest {
    private final JdbcTemplate jdbcTemplate;

    private FilmLikesService filmLikesService;
    private FilmsDbStorage filmsDbStorage;
    private UserDbStorage userDbStorage;

    private Film firstFilm;
    private User userFilmorate;

    @BeforeEach
    void setUp() {
        filmLikesService = new FilmLikesService(new FilmLikesDbStorage(jdbcTemplate));
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
    void addFilmLike_addingLikeFilm_UserAndFilmExist() {
        //given
        filmsDbStorage.add(firstFilm);
        userDbStorage.add(userFilmorate);
        Set<Long> likesFilmByFirstFilmBefore = filmLikesService.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmBefore.isEmpty());

        //when
        filmLikesService.addLikeFilm(firstFilm.getId(), userFilmorate.getId());

        //then
        Set<Long> likesFilmByFirstFilmAfter = filmLikesService.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmAfter.contains(userFilmorate.getId()));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении лайка, когда фильм не существует")
    void addLikeFilm_notAddingLikeFilm_filmNotExist() {
        //given
        userDbStorage.add(userFilmorate);

        //when+then
        long filmIdNotExist = 777L;
        assertThrows(DataIntegrityViolationException.class, () -> filmLikesService.addLikeFilm(filmIdNotExist, userFilmorate.getId()));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении лайка, когда пользователь не существует")
    void addLikeFilm_notAddingLikeFilm_userNotExist() {
        //given
        filmsDbStorage.add(firstFilm);

        //when+then
        long userIdNotExist = 777L;
        assertThrows(DataIntegrityViolationException.class, () -> filmLikesService.addLikeFilm(firstFilm.getId(), userIdNotExist));
    }

    @Test
    @DisplayName("Должен добавить фильму лайк, когда и фильм и пользователь существуют")
    void addFilmLikes_addingLikeFilm_UserAndFilmExist() {
        //given
        filmsDbStorage.add(firstFilm);
        userDbStorage.add(userFilmorate);
        Set<Long> likesFilmByFirstFilmBefore = filmLikesService.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmBefore.isEmpty());

        //when
        Set<Long> idUserLikes = new HashSet<>();
        idUserLikes.add(userFilmorate.getId());
        filmLikesService.addFilmLikes(firstFilm.getId(), idUserLikes);

        //then
        Set<Long> likesFilmByFirstFilmAfter = filmLikesService.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmAfter.contains(userFilmorate.getId()));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении лайка, когда фильм не существует")
    void addFilmLikes_notAddingLikeFilm_filmNotExist() {
        //given
        userDbStorage.add(userFilmorate);
        Set<Long> idUserLikes = new HashSet<>();
        idUserLikes.add(userFilmorate.getId());

        //when+then
        long filmIdNotExist = 777L;
        assertThrows(DataIntegrityViolationException.class, () -> filmLikesService.addFilmLikes(filmIdNotExist, idUserLikes));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении лайка, когда пользователь не существует")
    void addFilmLikes_notAddingLikeFilm_userNotExist() {
        //given
        filmsDbStorage.add(firstFilm);

        //when+then
        long userIdNotExist = 777L;
        Set<Long> idUserLikes = new HashSet<>();
        idUserLikes.add(userIdNotExist);
        assertThrows(DataIntegrityViolationException.class, () -> filmLikesService.addFilmLikes(firstFilm.getId(), idUserLikes));
    }

    @Test
    @DisplayName("Должен удалить фильму лайк, когда и фильм и пользователь существуют и добавлен лайк")
    void removeLikeFilm_removeLikeFilm_UserAndFilmExist() {
        //given
        filmsDbStorage.add(firstFilm);
        userDbStorage.add(userFilmorate);
        filmLikesService.addLikeFilm(firstFilm.getId(), userFilmorate.getId());
        Set<Long> likesFilmByFirstFilmBefore = filmLikesService.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmBefore.contains(userFilmorate.getId()));

        //when
        filmLikesService.removeLikeFilm(firstFilm.getId(), userFilmorate.getId());

        //then
        Set<Long> likesFilmByFirstFilmAfter = filmLikesService.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен удалить все лайки фильму, когда есть лайки")
    void deleteAllFilmLikesByFilmId_removeAllLikesFilmById_UserAndFilmExist() {
        //given
        filmsDbStorage.add(firstFilm);
        userDbStorage.add(userFilmorate);
        filmLikesService.addLikeFilm(firstFilm.getId(), userFilmorate.getId());
        Set<Long> likesFilmByFirstFilmBefore = filmLikesService.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmBefore.contains(userFilmorate.getId()));

        //when
        filmLikesService.deleteAllFilmLikesByFilmId(firstFilm.getId());

        //then
        Set<Long> likesFilmByFirstFilmAfter = filmLikesService.getFilmLikesByFilmId(firstFilm.getId());
        assertTrue(likesFilmByFirstFilmAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех лайков фильму, когда нет ни одного лайка")
    void getFilmLikesByFilmId_returnEmptyListAllLikesFilmById_notExistLikes() {
        //given
        filmsDbStorage.add(firstFilm);

        //when
        Set<Long> allLikesFirstFilm = filmLikesService.getFilmLikesByFilmId(firstFilm.getId());

        //then
        assertTrue(allLikesFirstFilm.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть не пустой список всех лайков фильму, когда лайки поставлены")
    void getFilmLikesByFilmId_returnNotEmptyListAllLikesFilmById_ExistLikes() {
        //given
        filmsDbStorage.add(firstFilm);
        userDbStorage.add(userFilmorate);
        filmLikesService.addLikeFilm(firstFilm.getId(), userFilmorate.getId());

        //when
        Set<Long> allLikesFirstFilm = filmLikesService.getFilmLikesByFilmId(firstFilm.getId());

        //then
        assertTrue(allLikesFirstFilm.contains(userFilmorate.getId()));
    }
}