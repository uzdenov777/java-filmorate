package ru.yandex.practicum.filmorate.storageTest.daoTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private FilmDbStorage filmDbStorage;
    private Film testFilm;

    @BeforeEach
    public void setUp() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate);

        testFilm = new Film();
        testFilm.setName("FilmTest");
        testFilm.setDescription("This is a test");
        testFilm.setReleaseDate(LocalDate.now());
        testFilm.setMpa(new Mpa(1, "G"));
        testFilm.setDuration(1L);
    }

    @DisplayName("Должен успешно добавить фильм и сохранить в него его новый ID")
    @Test
    public void add_addingFilm() {
        //given
        List<Film> allFilmsBefore = filmDbStorage.getAllFilms();
        assertNull(testFilm.getId()); // ID появится только после добавления
        assertTrue(allFilmsBefore.isEmpty());

        //when
        filmDbStorage.add(testFilm);

        //then
        List<Film> allFilmsAfter = filmDbStorage.getAllFilms();
        assertEquals(1L, testFilm.getId()); // теперь после добавления появился у фильма ID
        assertEquals(testFilm,  allFilmsAfter.get(0)); // так как кроме
    }

    @DisplayName("Должен успешно вернуть фильм, который был ранее добавлен")
    @Test
    public void getFilmById_existingFilm() {
        //given
        Film savedFilm = filmDbStorage.add(testFilm);

        //when
        Film foundFilm = filmDbStorage.getFilmById(testFilm.getId());

        //then
        assertEquals(foundFilm, savedFilm);
    }

    @DisplayName("Должен выбросить фильм, когда запрашиваем не существующий фильм по ID")
    @Test
    public void getFilmById_notExistingFilm() {
        //given
        Long notExistingFilmId = 999L;

        //when
        assertThrows(EmptyResultDataAccessException.class, () ->filmDbStorage.getFilmById(notExistingFilmId));
    }

    @DisplayName("Должен успешно вернуть список всех фильмов, когда фильмы добавлены")
    @Test
    public void getAllFilms_filmAdded() {
        //given
        List<Film> allFilmsBefore = filmDbStorage.getAllFilms();
        assertTrue(allFilmsBefore.isEmpty());

        //when
        filmDbStorage.add(testFilm);
        List<Film> allFilmsAfter = filmDbStorage.getAllFilms();

        //then
        assertEquals(1L, testFilm.getId());
        assertEquals(testFilm,  allFilmsAfter.get(0));
    }

    @DisplayName("Должен вернуть пустой список фильмов, когда фильмы не были добавлены")
    @Test
    public void getAllFilms_filmsNotAdded() {
        List<Film> allFilmsBefore = filmDbStorage.getAllFilms();
        assertTrue(allFilmsBefore.isEmpty());
    }

    @DisplayName("Должен успешно обновить фильм, когда фильм добавлен")
    @Test
    public void update_filmExists() {
        //given
        filmDbStorage.add(testFilm);
        Long filmId = testFilm.getId();
        Film foundFilmBefore = filmDbStorage.getFilmById(filmId);
        assertEquals(foundFilmBefore, testFilm);

        //when
        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
        updatedFilm.setName("updatedFilm");
        updatedFilm.setDescription("updatedFilm");
        updatedFilm.setReleaseDate(LocalDate.now());
        updatedFilm.setMpa(new Mpa(1, "G"));
        updatedFilm.setDuration(100L);
        filmDbStorage.update(updatedFilm);

        //then
        Film foundFilmAfter = filmDbStorage.getFilmById(filmId);
        assertEquals(updatedFilm, foundFilmAfter);
    }

    @DisplayName("Должен вернуть true при запросе на существование фильма по ID, когда фильм добавлен")
    @Test
    public void isFilmExists_filmExisting() {
        //given
        filmDbStorage.add(testFilm);
        Long filmId = testFilm.getId();

        //when
        boolean isFilmExists = filmDbStorage.isFilmExists(filmId);

        //then
        assertTrue(isFilmExists);
    }

    @DisplayName("Должен вернуть false при запросе на существование фильма по ID, когда фильм не добавлен добавлен")
    @Test
    public void isFilmExists_filmNotExisting() {
        //given
        Long filmId = 999L;

        //when
        boolean isFilmExists = filmDbStorage.isFilmExists(filmId);

        //then
        assertFalse(isFilmExists);
    }
}