//package ru.yandex.practicum.filmorate.storageTest;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.web.server.ResponseStatusException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryFilmStorage;
//
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class InMemoryFilmStorageTest {
//    InMemoryFilmStorage filmStorage;
//    Film firstFilm;
//
//    @BeforeEach
//    void setUp() {
//        filmStorage = new InMemoryFilmStorage();
//        firstFilm = new Film();
//        firstFilm.setName("FilmTest");
//        firstFilm.setDescription("This is a test");
//        firstFilm.setReleaseDate(LocalDate.now());
//        firstFilm.setDuration(1L);
//    }
//
//    @Test
//    @DisplayName("Должен успешно добавить пользователя в HashMap users")
//    void add_validFilm() {
//        //given
//        HashMap<Long, Film> filmsBefore = filmStorage.getFilms();
//        assertTrue(filmsBefore.isEmpty());
//
//        //when
//        filmStorage.add(firstFilm);
//
//        //then
//        HashMap<Long, Film> filmsAfter = filmStorage.getFilms();
//        assertTrue(filmsAfter.containsKey(firstFilm.getId()));
//    }
//
//    @Test
//    @DisplayName("Должен успешно обновить фильм, когда фильм для обновления существует")
//    void update_existBeforeFilm() {
//        //given
//        filmStorage.add(firstFilm);
//        HashMap<Long, Film> filmsBefore = filmStorage.getFilms();
//        assertEquals(firstFilm, filmsBefore.get(firstFilm.getId()));
//
//        //when
//        Film newVersionFilm = new Film();
//        newVersionFilm.setName("new FilmTest");
//        newVersionFilm.setDescription("new FilmTest");
//        newVersionFilm.setReleaseDate(LocalDate.now());
//        newVersionFilm.setDuration(1L);
//        newVersionFilm.setId(firstFilm.getId());
//        filmStorage.update(newVersionFilm);
//
//        //then
//        HashMap<Long, Film> filmsAfter = filmStorage.getFilms();
//        assertEquals(newVersionFilm, filmsAfter.get(firstFilm.getId()));
//    }
//
//    @Test
//    @DisplayName("Должен выбросит исключение ResponseStatusException, когда фильм для обновления не существует")
//    void update_throwResponseStatusException() {
//        //given
//        HashMap<Long, Film> filmsBefore = filmStorage.getFilms();
//        assertTrue(filmsBefore.isEmpty());
//
//        //when+then
//        assertThrows(ResponseStatusException.class, () -> filmStorage.update(firstFilm));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустой лист фильмов, когда не добавлено ни одного фильма")
//    void getAllFilms_returnEmptyListFilms() {
//        List<Film> films = filmStorage.getAllFilms();
//        assertTrue(films.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список c одним фильмов, когда фильм добавлен один")
//    void getAllFilms_returnListFilmSize1() {
//        //given
//        List<Film> filmsBefore = filmStorage.getAllFilms();
//        assertTrue(filmsBefore.isEmpty());
//
//        //when
//        filmStorage.add(firstFilm);
//
//        //then
//        List<Film> filmsAfter = filmStorage.getAllFilms();
//        assertTrue(filmsAfter.contains(firstFilm));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список c тремя фильмами, когда фильмы добавлены")
//    void getAllFilms_returnListFilmSize3() {
//        //given
//        List<Film> filmsBefore = filmStorage.getAllFilms();
//        assertTrue(filmsBefore.isEmpty());
//
//        //when
//        Film secondFilm = new Film();
//        Film newFilm = new Film();
//        secondFilm.setReleaseDate(LocalDate.now());
//        newFilm.setReleaseDate(LocalDate.now());
//        filmStorage.add(firstFilm);
//        filmStorage.add(secondFilm);
//        filmStorage.add(newFilm);
//
//        //then
//        List<Film> filmsAfter = filmStorage.getAllFilms();
//        assertTrue(filmsAfter.contains(firstFilm));
//        assertTrue(filmsAfter.contains(secondFilm));
//        assertTrue(filmsAfter.contains(newFilm));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть фильм по ID, когда фильм добавлен")
//    void getFilmById_returnExistFilmByID() {
//        //given
//        List<Film> filmsBefore = filmStorage.getAllFilms();
//        assertTrue(filmsBefore.isEmpty());
//
//        //when
//        filmStorage.add(firstFilm);
//
//        //then
//        Film restoredFilm = filmStorage.getFilmById(firstFilm.getId());
//        assertEquals(firstFilm, restoredFilm);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть null, когда фильм не добавлен")
//    void getFilmById_returnNull() {
//        Film restoredFilm = filmStorage.getFilmById(firstFilm.getId());
//        assertNull(restoredFilm);
//    }
//
//    @Test
//    @DisplayName("Выбросит исключение ResponseStatusException, когда releaseDate раньше или ровно 1895-12-28")
//    void setReleaseDateEarlier1895_12_28() {
//        //given
//        firstFilm.setId(1);
//        firstFilm.setName("FilmTest");
//        firstFilm.setDuration(20L);
//        firstFilm.setDescription("This is a test");
//
//        //when+then
//        LocalDate invalidReleaseDate = LocalDate.of(1895, 12, 28);
//        firstFilm.setReleaseDate(invalidReleaseDate);
//        assertThrows(ResponseStatusException.class, () -> filmStorage.add(firstFilm));
//
//        //when+then
//        LocalDate releaseDateBefore1895 = LocalDate.of(1895, 12, 27);
//        firstFilm.setReleaseDate(releaseDateBefore1895);
//        assertThrows(ResponseStatusException.class, () -> filmStorage.add(firstFilm));
//    }
//}