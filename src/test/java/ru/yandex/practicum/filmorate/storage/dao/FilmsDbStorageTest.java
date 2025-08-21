package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class FilmsDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private FilmsDbStorage filmsDbStorage;
    private Film testFilm;

    @BeforeEach
    void setUp() {
        filmsDbStorage = new FilmsDbStorage(jdbcTemplate);

        testFilm = new Film();
        testFilm.setName("FilmTest");
        testFilm.setDescription("This is a test");
        testFilm.setReleaseDate(LocalDate.now());
        testFilm.setMpa(new Mpa(1, "G"));
        testFilm.setDuration(1L);
    }

    @DisplayName("Должен успешно добавить фильм и сохранить в него его новый ID")
    @Test
    void add_addingFilm() {
        //given
        List<Film> allFilmsBefore = filmsDbStorage.getAllFilms();
        assertNull(testFilm.getId()); // ID появится только после добавления
        assertTrue(allFilmsBefore.isEmpty());

        //when
        filmsDbStorage.add(testFilm);

        //then
        List<Film> allFilmsAfter = filmsDbStorage.getAllFilms();
        assertEquals(1L, testFilm.getId()); // теперь после добавления появился у фильма ID
        assertEquals(testFilm, allFilmsAfter.get(0)); // так как кроме
    }

    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда у фильма отсутствует Name")
    @Test
    void add_notAddingFilm_nameNull() {
        //given
        List<Film> allFilmsBefore = filmsDbStorage.getAllFilms();
        assertTrue(allFilmsBefore.isEmpty());

        //when+then
        testFilm.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> filmsDbStorage.add(testFilm));
    }

    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда у фильма отсутствует Description")
    @Test
    void add_notAddingFilm_descriptionNull() {
        //given
        List<Film> allFilmsBefore = filmsDbStorage.getAllFilms();
        assertTrue(allFilmsBefore.isEmpty());

        //when+then
        testFilm.setDescription(null);
        assertThrows(DataIntegrityViolationException.class, () -> filmsDbStorage.add(testFilm));
    }

    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда у фильма отсутствует ReleaseDate")
    @Test
    void add_notAddingFilm_releaseDateNull() {
        //given
        List<Film> allFilmsBefore = filmsDbStorage.getAllFilms();
        assertTrue(allFilmsBefore.isEmpty());

        //when+then
        testFilm.setReleaseDate(null);
        assertThrows(DataIntegrityViolationException.class, () -> filmsDbStorage.add(testFilm));
    }

    @DisplayName("Должен выбросить исключение DataIntegrityViolationException, когда у фильма отсутствует duration")
    @Test
    void add_notAddingFilm_durationNull() {
        //given
        List<Film> allFilmsBefore = filmsDbStorage.getAllFilms();
        assertTrue(allFilmsBefore.isEmpty());

        //when+then
        testFilm.setDuration(null);
        assertThrows(DataIntegrityViolationException.class, () -> filmsDbStorage.add(testFilm));
    }

    @DisplayName("Должен успешно вернуть фильм, который был ранее добавлен")
    @Test
    void getFilmById_existingFilm() {
        //given
        Film savedFilm = filmsDbStorage.add(testFilm);

        //when
        Film foundFilm = filmsDbStorage.getFilmById(testFilm.getId());

        //then
        assertEquals(foundFilm, savedFilm);
    }

    @DisplayName("Должен выбросить фильм, когда запрашиваем не существующий фильм по ID")
    @Test
    void getFilmById_notExistingFilm() {
        //given
        Long notExistingFilmId = 999L;

        //when
        assertThrows(EmptyResultDataAccessException.class, () -> filmsDbStorage.getFilmById(notExistingFilmId));
    }

    @DisplayName("Должен успешно вернуть список всех фильмов, когда фильмы добавлены")
    @Test
    void getAllFilms_filmAdded() {
        //given
        List<Film> allFilmsBefore = filmsDbStorage.getAllFilms();
        assertTrue(allFilmsBefore.isEmpty());

        //when
        filmsDbStorage.add(testFilm);
        List<Film> allFilmsAfter = filmsDbStorage.getAllFilms();

        //then
        assertEquals(1L, testFilm.getId());
        assertEquals(testFilm, allFilmsAfter.get(0));
    }

    @DisplayName("Должен вернуть пустой список фильмов, когда фильмы не были добавлены")
    @Test
    void getAllFilms_filmsNotAdded() {
        List<Film> allFilmsBefore = filmsDbStorage.getAllFilms();
        assertTrue(allFilmsBefore.isEmpty());
    }

    @DisplayName("Должен успешно обновить фильм, когда фильм добавлен")
    @Test
    void update_filmExists() {
        //given
        filmsDbStorage.add(testFilm);
        Long filmId = testFilm.getId();
        Film foundFilmBefore = filmsDbStorage.getFilmById(filmId);
        assertEquals(foundFilmBefore, testFilm);

        //when
        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
        updatedFilm.setName("updatedFilm");
        updatedFilm.setDescription("updatedFilm");
        updatedFilm.setReleaseDate(LocalDate.now());
        updatedFilm.setMpa(new Mpa(1, "G"));
        updatedFilm.setDuration(100L);
        filmsDbStorage.update(updatedFilm);

        //then
        Film foundFilmAfter = filmsDbStorage.getFilmById(filmId);
        assertEquals(updatedFilm, foundFilmAfter);
    }

    @DisplayName("Должен вернуть true при запросе на существование фильма по ID, когда фильм добавлен")
    @Test
    void isFilmExists_filmExisting() {
        //given
        filmsDbStorage.add(testFilm);
        Long filmId = testFilm.getId();

        //when
        boolean isFilmExists = filmsDbStorage.isFilmExists(filmId);

        //then
        assertTrue(isFilmExists);
    }

    @DisplayName("Должен вернуть false при запросе на существование фильма по ID, когда фильм не добавлен добавлен")
    @Test
    void isFilmExists_filmNotExisting() {
        //given
        Long filmId = 999L;

        //when
        boolean isFilmExists = filmsDbStorage.isFilmExists(filmId);

        //then
        assertFalse(isFilmExists);
    }
}