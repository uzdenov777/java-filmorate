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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class FilmGenresDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private FilmGenresDbStorage filmGenresDbStorage;
    private FilmsDbStorage filmsDbStorage;

    private Film firstFilm;

    @BeforeEach
    void setup() {
        filmGenresDbStorage = new FilmGenresDbStorage(jdbcTemplate);
        filmsDbStorage = new FilmsDbStorage(jdbcTemplate);

        firstFilm = new Film();
        firstFilm.setName("FirstFilm");
        firstFilm.setDescription("FirstFilmDescription");
        firstFilm.setDuration(10L);
        firstFilm.setReleaseDate(LocalDate.now());
        firstFilm.setMpa(new Mpa(1, "G"));
    }

    @Test
    @DisplayName("Должен добавить жанр фильму, когда и фильм и жанр существуют")
    void addFilmGenre_addingFilmGenre_GenreAndFilmExist() {
        //given
        filmsDbStorage.add(firstFilm);
        List<Genre> genresFirstFilmBefore = filmGenresDbStorage.getGenresByFilmId(firstFilm.getId());
        assertTrue(genresFirstFilmBefore.isEmpty());

        //when
        filmGenresDbStorage.addFilmGenre(firstFilm.getId(), 1);

        //then
        List<Genre> genresFirstFilmAfter = filmGenresDbStorage.getGenresByFilmId(firstFilm.getId());
        assertEquals(1, genresFirstFilmAfter.size());
        Genre genreFirstFilm = genresFirstFilmAfter.get(0);
        assertEquals(1, genreFirstFilm.getId());
        assertEquals("Комедия", genreFirstFilm.getName());
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении фильму жанра, когда фильма с таким ID нет")
    void addFilmGenre_notAddingFilmGenre_filmNotExist() {
        //when+then
        long filmIdNotExit = 777L;
        assertThrows(DataIntegrityViolationException.class, () -> filmGenresDbStorage.addFilmGenre(filmIdNotExit, 1));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении фильму жанра, когда жанра с таким ID нет")
    void addFilmGenre_notAddingFilmGenre_genreNotExist() {
        //given
        filmsDbStorage.add(firstFilm);
        List<Genre> genresFirstFilmBefore = filmGenresDbStorage.getGenresByFilmId(firstFilm.getId());
        assertTrue(genresFirstFilmBefore.isEmpty());

        //when+then
        int genreIdNotExit = 777;
        assertThrows(DataIntegrityViolationException.class, () -> filmGenresDbStorage.addFilmGenre(firstFilm.getId(), genreIdNotExit));
    }

    @Test
    @DisplayName("Должен удалить удалить все жанры фильма по его ID")
    void deleteFilmGenresByFilmId_genreExist() {
        //given
        filmsDbStorage.add(firstFilm);
        filmGenresDbStorage.addFilmGenre(firstFilm.getId(), 1);
        List<Genre> genresFirstFilmBefore = filmGenresDbStorage.getGenresByFilmId(firstFilm.getId());
        assertEquals(1, genresFirstFilmBefore.get(0).getId());

        //when
        filmGenresDbStorage.deleteFilmGenresByFilmId(firstFilm.getId());

        //then
        List<Genre> genresFirstFilmAfter = filmGenresDbStorage.getGenresByFilmId(firstFilm.getId());
        assertTrue(genresFirstFilmAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть не пустой список жанров фильма, когда жанры добавлены")
    void getGenresByFilmId_genreAdding() {
        //given
        filmsDbStorage.add(firstFilm);
        filmGenresDbStorage.addFilmGenre(firstFilm.getId(), 1);

        //when
        List<Genre> genresFirstFilm = filmGenresDbStorage.getGenresByFilmId(firstFilm.getId());

        //then
        assertEquals(1, genresFirstFilm.get(0).getId());
    }

    @Test
    @DisplayName("Должен вернуть пустой список жанров фильма, когда жанры не добавлены")
    void getGenresByFilmId_genreNotAdding() {
        //given
        filmsDbStorage.add(firstFilm);

        //when
        List<Genre> genresFirstFilm = filmGenresDbStorage.getGenresByFilmId(firstFilm.getId());

        //then
        assertTrue(genresFirstFilm.isEmpty());
    }
}