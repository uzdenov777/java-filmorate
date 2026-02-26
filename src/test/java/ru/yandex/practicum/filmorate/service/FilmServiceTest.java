//package ru.yandex.practicum.filmorate.service;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.web.server.ResponseStatusException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.Mpa;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.storage.dao.*;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@JdbcTest
//class FilmServiceTest {
//    private final JdbcTemplate jdbcTemplate;
//
//    private FilmService filmService;
//    private Film firstFilm;
//    private Film secondFilm;
//
//    private UserDbStorage userDbStorage;
//    private User userFirst;
//
//    @BeforeEach
//    void setUp() {
//        FilmsDbStorage filmsDbStorage = new FilmsDbStorage(jdbcTemplate);
//        userDbStorage = new UserDbStorage(jdbcTemplate);
//
//        UserService userService = new UserService(new UserDbStorage(jdbcTemplate), new FriendsServer(new FriendsDbStorage(jdbcTemplate)));
//        FilmLikesService filmLikesService = new FilmLikesService(new FilmLikesDbStorage(jdbcTemplate));
//        FilmGenresService filmGenresService = new FilmGenresService(new FilmGenresDbStorage(jdbcTemplate));
//        GenresService genresService = new GenresService(new GenresDbStorage(jdbcTemplate));
//        MpaService mpaService = new MpaService(new MpaDbStorage(jdbcTemplate));
//
//        filmService = new FilmService(filmsDbStorage, userService, filmLikesService, filmGenresService, genresService, mpaService);
//
//        firstFilm = new Film();
//        firstFilm.setName("FilmTest");
//        firstFilm.setDescription("This is a test");
//        firstFilm.setReleaseDate(LocalDate.now());
//        firstFilm.setMpa(new Mpa(1, "G"));
//        firstFilm.setDuration(1L);
//
//        secondFilm = new Film();
//        secondFilm.setName("New Film");
//        secondFilm.setDescription("New Description");
//        secondFilm.setReleaseDate(LocalDate.now());
//        secondFilm.setMpa(new Mpa(2, "PG"));
//        secondFilm.setDuration(1L);
//
//        userFirst = new User();
//        userFirst.setName("First");
//        userFirst.setEmail("newEmail@gmail.com");
//        userFirst.setLogin("FirstLogin");
//        userFirst.setBirthday(LocalDate.now());
//    }
//
//    @Test
//    @DisplayName("Должен успешно добавить фильм")
//    void add_testAddFilm() {
//        //given
//        List<Film> allFilmsBefore = filmService.getAllFilms();
//        Long firstFilmIdBefore = firstFilm.getId();
//        assertNull(firstFilmIdBefore); // ID появится только после добавления
//        assertTrue(allFilmsBefore.isEmpty());
//
//        //when
//        filmService.add(firstFilm);
//
//        //then
//        List<Film> allFilmsAfter = filmService.getAllFilms();
//        Long firstFilmIdAfter = firstFilm.getId();
//        Film retFirstFilm = allFilmsAfter.get(0);
//        assertEquals(1L, firstFilmIdAfter); // теперь после добавления появился у фильма ID
//        assertEquals(firstFilm, retFirstFilm);
//    }
//
//    @Test
//    @DisplayName("Должен успешно обновить фильм, когда фильм для обновления был ранее добавлен")
//    void update_existingFilmToUpdate() {
//        //given
//        filmService.add(firstFilm);
//        Long firstFilmId = firstFilm.getId();
//        List<Film> before = filmService.getAllFilms();
//        Film retFirstFilm = before.get(0);
//        assertEquals(1, before.size());
//        assertEquals(firstFilm, retFirstFilm);
//
//        //when
//        Film newFilm = new Film();
//        newFilm.setId(firstFilmId);
//        newFilm.setName("New Film");
//        newFilm.setDescription("New Description");
//        newFilm.setReleaseDate(LocalDate.now());
//        newFilm.setMpa(new Mpa(2, "PG"));
//        newFilm.setDuration(1L);
//        filmService.update(newFilm);
//
//        //then
//        List<Film> filmList = filmService.getAllFilms();
//        Film retNewFilm = filmList.get(0);
//        assertEquals(1, filmList.size());
//        assertEquals(newFilm, retNewFilm);
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда фильма для обновления с таким ID не добавлен")
//    void update_notUpdatedFilm_noExistingFilmToUpdate() {
//        //when+then
//        assertThrows(ResponseStatusException.class, () -> filmService.update(firstFilm));
//    }
//
//    @DisplayName("Должен успешно вернуть фильм, который был ранее добавлен")
//    @Test
//    void getFilmById_existingFilm() {
//        //given
//        Film savedFilm = filmService.add(firstFilm);
//
//        //when
//        Long firstFilmId = firstFilm.getId();
//        Film foundFilm = filmService.getFilmById(firstFilmId);
//
//        //then
//        assertEquals(foundFilm, savedFilm);
//    }
//
//    @DisplayName("Должен выбросить фильм, когда запрашиваем не существующий фильм по ID")
//    @Test
//    void getFilmById_notExistingFilm() {
//        //given
//        Long notExistingFilmId = 999L;
//
//        //when
//        assertThrows(ResponseStatusException.class, () -> filmService.getFilmById(notExistingFilmId));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список всех добавленных фильмов, когда фильмы добавлены")
//    void getAllFilms_getNotEmptyListAddFilms() {
//        //given
//        List<Film> allFilmsListBefore = filmService.getAllFilms();
//        assertTrue(allFilmsListBefore.isEmpty());
//
//        //when
//        filmService.add(firstFilm);
//        List<Film> filmList = filmService.getAllFilms();
//
//        //then
//        Film resFilm = filmList.get(0);
//        assertEquals(1, filmList.size());
//        assertEquals(firstFilm, resFilm);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустой список всех фильмов, когда фильмы не добавлены")
//    void
//    getAllFilms_getEmptyListAddFilms() {
//        List<Film> filmList = filmService.getAllFilms();
//        assertTrue(filmList.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен успешно добавить id пользователя, который поставил лайк, когда фильм и пользователь существуют")
//    void addLikeToFilm_trueAddLikeFilm() {
//        //given
//        userDbStorage.add(userFirst);
//        filmService.add(firstFilm);
//        long idFirstFilm = firstFilm.getId();
//        long idUser = userFirst.getId();
//        Set<Long> likesFirstFilmBefore = filmService.getLikersIdsByFilmId(idFirstFilm);
//        assertTrue(likesFirstFilmBefore.isEmpty());
//
//        //when
//        filmService.addLikeToFilm(idFirstFilm, idUser);
//
//        //then
//        Set<Long> likesFirstFilmAfter = filmService.getLikersIdsByFilmId(idFirstFilm);
//        assertTrue(likesFirstFilmAfter.contains(idUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда фильм для лайка не добавлен")
//    void addLikeToFilm_failAddLikeFilm_NotAddingFilm() {
//        //given
//        userDbStorage.add(userFirst);
//
//        //when+then
//        long idNotExistFilm = 777; // ID фильма, который не был добавлен
//        long idUser = userFirst.getId();
//        assertThrows(ResponseStatusException.class, () -> filmService.addLikeToFilm(idNotExistFilm, idUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда пользователь не добавлен")
//    void addLikeToFilm_failAddLikeFilm_NotAddingUser() {
//        //given
//        filmService.add(firstFilm);
//        Long firstFilmId = firstFilm.getId();
//        Set<Long> likesFirstFilmBefore = filmService.getLikersIdsByFilmId(firstFilmId);
//        assertTrue(likesFirstFilmBefore.isEmpty());
//
//        //when+then
//        long idNotExistUser = 777L;// ID пользователя, который не был добавлен
//        assertThrows(ResponseStatusException.class, () -> filmService.addLikeToFilm(firstFilmId, idNotExistUser));
//    }
//
//    @Test
//    @DisplayName("Должен успешно удалить id пользователя, который поставил лайк, когда фильм и пользователь существуют")
//    void removeLikeToFilm_trueAddLikeFilm() {
//        //given
//        userDbStorage.add(userFirst);
//        filmService.add(firstFilm);
//        Long userFirstId = userFirst.getId();
//        Long firstFilmId = firstFilm.getId();
//        Set<Long> likesFirstFilmBefore = filmService.getLikersIdsByFilmId(firstFilmId);
//        likesFirstFilmBefore.add(userFirstId);
//
//        assertTrue(likesFirstFilmBefore.contains(userFirstId));
//
//        //when
//        long idFirstFilm = firstFilmId;
//
//        filmService.removeLikeToFilm(idFirstFilm, userFirstId);
//
//        //then
//        Set<Long> likesFirstFilmAfter = filmService.getLikersIdsByFilmId(firstFilmId);
//        assertFalse(likesFirstFilmAfter.contains(userFirstId));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда фильм для удаления лайка не добавлен")
//    void removeLikeToFilm_falseAddLikeFilm_NotAddingFilm() {
//        //given
//        userDbStorage.add(userFirst);
//
//        //when+then
//        long idNotExistFilm = 777; // ID фильма, который не был добавлен
//        long idUser = userFirst.getId();
//        assertThrows(ResponseStatusException.class, () -> filmService.removeLikeToFilm(idNotExistFilm, idUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда пользователь для удаления лайка не добавлен")
//    void removeLikeToFilm_falseAddLikeFilm_NotAddingUser() {
//        //given
//        filmService.add(firstFilm);
//        Long firstFilmId = firstFilm.getId();
//        Set<Long> likesFirstFilmBefore = filmService.getLikersIdsByFilmId(firstFilmId);
//        assertTrue(likesFirstFilmBefore.isEmpty());
//
//        //when+then
//        long idNotExistUser = 777L;// ID пользователя, который не был добавлен
//        assertThrows(ResponseStatusException.class, () -> filmService.removeLikeToFilm(firstFilmId, idNotExistUser));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список топ 1 фильмов по лайкам, когда несколько фильмов добавлены")
//    void getListTopPopularFilms_getTop1() {
//        //given
//        userDbStorage.add(userFirst);
//        filmService.add(firstFilm);
//        filmService.add(secondFilm);
//        Long userFirstId = userFirst.getId();
//        Long firstFilmId = firstFilm.getId();
//        List<Film> topPopularFilms = filmService.getAllFilms();
//        assertTrue(topPopularFilms.contains(firstFilm));
//        assertTrue(topPopularFilms.contains(secondFilm));
//
//        //when
//        filmService.addLikeToFilm(firstFilmId, userFirstId);
//        List<Film> top1 = filmService.getListTopPopularFilms(1);
//
//        //then
//        Film resFilm = top1.get(0);
//        Long idResFilm = resFilm.getId();
//        assertEquals(firstFilmId, idResFilm);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список топ 3 фильмов по лайкам, когда несколько фильмов добавлены")
//    void getListTopPopularFilms_getTop3() {
//        //given
//        User newUser = new User();
//        newUser.setName("newUser");
//        newUser.setEmail("newUser@email");
//        newUser.setLogin("newUserLogin");
//        newUser.setBirthday(LocalDate.now());
//        User secondUser = new User();
//        secondUser.setName("secondUser");
//        secondUser.setEmail("secondUser@email");
//        secondUser.setLogin("secondUserLogin");
//        secondUser.setBirthday(LocalDate.now());
//        Film newFilm = new Film();
//        newFilm.setReleaseDate(LocalDate.now());
//        newFilm.setName("newFilm");
//        newFilm.setDescription("newFilmDescription");
//        newFilm.setDuration(1L);
//        newFilm.setMpa(new Mpa(1, "G"));
//
//        userDbStorage.add(secondUser);
//        userDbStorage.add(newUser);
//        userDbStorage.add(userFirst);
//        filmService.add(firstFilm);
//        filmService.add(secondFilm);
//        filmService.add(newFilm);
//        Long secondUserId = secondUser.getId();
//        Long newUserId = newUser.getId();
//        Long userFirstId = userFirst.getId();
//        Long idFirstFilm = firstFilm.getId();
//        Long idSecondFilm = secondFilm.getId();
//        Long idNewFilm = newFilm.getId();
//
//        List<Film> topPopularFilms = filmService.getListTopPopularFilms(3);
//        assertEquals(3, topPopularFilms.size());
//
//        //when
//        filmService.addLikeToFilm(idFirstFilm, userFirstId);
//        filmService.addLikeToFilm(idSecondFilm, secondUserId);
//        filmService.addLikeToFilm(idSecondFilm, newUserId);
//        filmService.addLikeToFilm(idNewFilm, newUserId);
//        filmService.addLikeToFilm(idNewFilm, secondUserId);
//        filmService.addLikeToFilm(idNewFilm, userFirstId);
//        List<Film> top3 = filmService.getListTopPopularFilms(3);
//
//        //then
//        Film retNewFilm = top3.get(0);
//        Film retSecondFilm = top3.get(1);
//        Film retFirstFilm = top3.get(2);
//        Long retNewFilmId = retNewFilm.getId();
//        Long retSecondFilmId = retSecondFilm.getId();
//        Long retFirstFilmId = retFirstFilm.getId();
//
//        assertEquals(3, top3.size());
//        assertEquals(idNewFilm, retNewFilmId);
//        assertEquals(idSecondFilm, retSecondFilmId);
//        assertEquals(idFirstFilm, retFirstFilmId);
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда не добавлено еще ни одного фильма для получения TOP-а")
//    void getListTopPopularFilms_throwResponseStatusException() {
//        //when+then
//        assertThrows(ResponseStatusException.class, () -> filmService.getListTopPopularFilms(2));
//    }
//}