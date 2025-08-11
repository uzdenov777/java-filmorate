//package ru.yandex.practicum.filmorate.serviceTest;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.web.server.ResponseStatusException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.service.FilmService;
//import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryFilmStorage;
//import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryUserStorage;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FilmServiceTest {
//    FilmService filmService;
//    Film firstFilm;
//    Film secondFilm;
//
//    InMemoryUserStorage userStorage;
//    User userFirst;
//
//    @BeforeEach
//    void setUp() {
//        userStorage = new InMemoryUserStorage();
//        filmService = new FilmService(new InMemoryFilmStorage(), userStorage);
//
//        firstFilm = new Film();
//        firstFilm.setName("FilmTest");
//        firstFilm.setDescription("This is a test");
//        firstFilm.setReleaseDate(LocalDate.now());
//        firstFilm.setDuration(1L);
//        secondFilm = new Film();
//        secondFilm.setName("New Film");
//        secondFilm.setDescription("New Description");
//        secondFilm.setReleaseDate(LocalDate.now());
//        secondFilm.setDuration(1L);
//
//        userFirst = new User();
//    }
//
//    @Test
//    @DisplayName("Должен успешно добавить фильм")
//    void add_testAddFilm() {
//        //given
//        List<Film> before = filmService.getAllFilms();
//        assertTrue(before.isEmpty());
//
//        //when
//        filmService.add(firstFilm);
//
//        //then
//        List<Film> filmList = filmService.getAllFilms();
//        assertEquals(1, filmList.size());
//        assertEquals(firstFilm, filmList.get(0));
//    }
//
//    @Test
//    @DisplayName("Должен успешно обновить фильм, когда фильм для обновления был ранее добавлен")
//    void update_existingFilmToUpdate() {
//        //given
//        filmService.add(firstFilm);
//        List<Film> before = filmService.getAllFilms();
//        assertEquals(1, before.size());
//        assertEquals(firstFilm, before.get(0));
//
//        //when
//        Film newFilm = new Film();
//        newFilm.setId(firstFilm.getId());
//        newFilm.setName("New Film");
//        newFilm.setDescription("New Description");
//        newFilm.setReleaseDate(LocalDate.now());
//        newFilm.setDuration(1L);
//        filmService.update(newFilm);
//
//        //then
//        List<Film> filmList = filmService.getAllFilms();
//        assertEquals(1, filmList.size());
//        assertEquals(newFilm, filmList.get(0));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда фильма для обновления с таким ID не добавлен")
//    void update_notUpdatedFilm_noExistingFilmToUpdate() {
//        //when+then
//        assertThrows(ResponseStatusException.class, () -> filmService.update(firstFilm));
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
//        filmService.add(secondFilm);
//
//        //then
//        List<Film> filmList = filmService.getAllFilms();
//        assertEquals(2, filmList.size());
//        assertEquals(firstFilm, filmList.get(0));
//        assertEquals(secondFilm, filmList.get(1));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустой список всех фильмов, когда фильмы не добавлены")
//    void getAllFilms_getEmptyListAddFilms() {
//        List<Film> filmList = filmService.getAllFilms();
//        assertTrue(filmList.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен успешно добавить id пользователя, который поставил лайк, когда фильм и пользователь существуют")
//    void addLikeFilm_trueAddLikeFilm() {
//        //given
//        userStorage.add(userFirst);
//        filmService.add(firstFilm);
//        Set<Long> likesFirstFilmBefore = firstFilm.getLikesFromUsers();
//        assertTrue(likesFirstFilmBefore.isEmpty());
//
//        //when
//        long idFirstFilm = firstFilm.getId();
//        long idUser = userFirst.getId();
//        filmService.addLikeFilm(idFirstFilm, idUser);
//
//        //then
//        Set<Long> likesFirstFilmAfter = firstFilm.getLikesFromUsers();
//        assertTrue(likesFirstFilmAfter.contains(idUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда фильм для лайка не добавлен")
//    void addLikeFilm_falseAddLikeFilm_NotAddingFilm() {
//        //given
//        userStorage.add(userFirst);
//        firstFilm.setId(255);
//        Set<Long> likesFirstFilmBefore = firstFilm.getLikesFromUsers();
//        assertTrue(likesFirstFilmBefore.isEmpty());
//
//        //when+then
//        long idFirstFilm = firstFilm.getId(); // ID фильма, который не был добавлен
//        long idUser = userFirst.getId();
//        assertThrows(ResponseStatusException.class, () -> filmService.addLikeFilm(idFirstFilm, idUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда пользователь не добавлен")
//    void addLikeFilm_falseAddLikeFilm_NotAddingUser() {
//        //given
//        filmService.add(firstFilm);
//        userFirst.setId(255L);
//        Set<Long> likesFirstFilmBefore = firstFilm.getLikesFromUsers();
//        assertTrue(likesFirstFilmBefore.isEmpty());
//
//        //when+then
//        long idFirstFilm = firstFilm.getId();
//        long idUser = userFirst.getId();// ID пользователя, который не был добавлен
//        assertThrows(ResponseStatusException.class, () -> filmService.addLikeFilm(idFirstFilm, idUser));
//    }
//
//    @Test
//    @DisplayName("Должен успешно удалить id пользователя, который поставил лайк, когда фильм и пользователь существуют")
//    void removeLikeFilm_trueAddLikeFilm() {
//        //given
//        userStorage.add(userFirst);
//        filmService.add(firstFilm);
//        Set<Long> likesFirstFilmBefore = firstFilm.getLikesFromUsers();
//        likesFirstFilmBefore.add(userFirst.getId());
//        assertTrue(likesFirstFilmBefore.contains(userFirst.getId()));
//
//        //when
//        long idFirstFilm = firstFilm.getId();
//        long idUser = userFirst.getId();
//        filmService.removeLikeFilm(idFirstFilm, idUser);
//
//        //then
//        Set<Long> likesFirstFilmAfter = firstFilm.getLikesFromUsers();
//        assertFalse(likesFirstFilmAfter.contains(idUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда фильм для удаления лайка не добавлен")
//    void removeLikeFilm_falseAddLikeFilm_NotAddingFilm() {
//        //given
//        userStorage.add(userFirst);
//        firstFilm.setId(255);
//        Set<Long> likesFirstFilmBefore = firstFilm.getLikesFromUsers();
//        assertTrue(likesFirstFilmBefore.isEmpty());
//
//        //when+then
//        long idFirstFilm = firstFilm.getId(); // ID фильма, который не был добавлен
//        long idUser = userFirst.getId();
//        assertThrows(ResponseStatusException.class, () -> filmService.removeLikeFilm(idFirstFilm, idUser));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда пользователь для удаления лайка не добавлен")
//    void removeLikeFilm_falseAddLikeFilm_NotAddingUser() {
//        //given
//        filmService.add(firstFilm);
//        userFirst.setId(255L);
//        Set<Long> likesFirstFilmBefore = firstFilm.getLikesFromUsers();
//        assertTrue(likesFirstFilmBefore.isEmpty());
//
//        //when+then
//        long idFirstFilm = firstFilm.getId();
//        long idUser = userFirst.getId();// ID пользователя, который не был добавлен
//        assertThrows(ResponseStatusException.class, () -> filmService.removeLikeFilm(idFirstFilm, idUser));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список топ 1 фильмов по лайкам, когда несколько фильмов добавлены")
//    void getListTopPopularFilms_getTop1() {
//        //given
//        userStorage.add(userFirst);
//        filmService.add(firstFilm);
//        filmService.add(secondFilm);
//        List<Film> topPopularFilms = filmService.getListTopPopularFilms(2);
//        assertTrue(topPopularFilms.contains(firstFilm));
//        assertTrue(topPopularFilms.contains(secondFilm));
//
//        //when
//        filmService.addLikeFilm(firstFilm.getId(), userFirst.getId());
//
//        //then
//        List<Film> top1 = filmService.getListTopPopularFilms(1);
//        assertTrue(top1.contains(firstFilm));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список топ 3 фильмов по лайкам, когда несколько фильмов добавлены")
//    void getListTopPopularFilms_getTop3() {
//        //given
//        User newUser = new User();
//        User secondUser = new User();
//        userStorage.add(secondUser);
//        userStorage.add(newUser);
//        userStorage.add(userFirst);
//
//        Film newFilm = new Film();
//        newFilm.setReleaseDate(LocalDate.now());
//        filmService.add(firstFilm);
//        filmService.add(secondFilm);
//        filmService.add(newFilm);
//
//        List<Film> topPopularFilms = filmService.getListTopPopularFilms(3);
//        assertTrue(topPopularFilms.contains(firstFilm));
//        assertTrue(topPopularFilms.contains(secondFilm));
//        assertTrue(topPopularFilms.contains(newFilm));
//
//        //when
//        filmService.addLikeFilm(firstFilm.getId(), userFirst.getId());
//        filmService.addLikeFilm(secondFilm.getId(), secondUser.getId());
//        filmService.addLikeFilm(secondFilm.getId(), newUser.getId());
//        filmService.addLikeFilm(newFilm.getId(), newUser.getId());
//        filmService.addLikeFilm(newFilm.getId(), secondUser.getId());
//        filmService.addLikeFilm(newFilm.getId(), userFirst.getId());
//
//        //then
//        List<Film> top3 = filmService.getListTopPopularFilms(3);
//        assertEquals(3, top3.size());
//        assertEquals(newFilm, top3.get(0));
//        assertEquals(secondFilm, top3.get(1));
//        assertEquals(firstFilm, top3.get(2));
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда не добавлено еще ни одного фильма для получения TOP-а")
//    void getListTopPopularFilms_throwResponseStatusException() {
//        //when+then
//        assertThrows(ResponseStatusException.class, () -> filmService.getListTopPopularFilms(2));
//    }
//}