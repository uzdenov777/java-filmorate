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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenresDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmsDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class FilmGenresServiceTest {

    private final JdbcTemplate jdbcTemplate;

    private FilmGenresService filmGenresService;
    private FilmsDbStorage filmsDbStorage;

    private Film firstFilm;

    @BeforeEach
    void setup() {
        filmGenresService = new FilmGenresService(new FilmGenresDbStorage(jdbcTemplate));
        filmsDbStorage = new FilmsDbStorage(jdbcTemplate);

        firstFilm = new Film();
        firstFilm.setName("FirstFilm");
        firstFilm.setDescription("FirstFilmDescription");
        firstFilm.setDuration(10L);
        firstFilm.setReleaseDate(LocalDate.now());
        firstFilm.setMpa(new Mpa(1, "G"));
    }

    @Test
    @DisplayName("Должен добавить все жанры переданные с списка, когда список не пустой и жанр и фильм существуют")
    void addFilmGenres_addingFilmGenre_listNotEmpty() {
        //given
        filmsDbStorage.add(firstFilm);
        Long firstFilmId = firstFilm.getId();
        Set<Genre> genresFirstFilmBefore = filmGenresService.getGenresByFilmId(firstFilmId);
        assertTrue(genresFirstFilmBefore.isEmpty());

        //when
        Genre newGenre = new Genre(1, "Комедия");
        Set<Genre> filmGenres = new HashSet<>();
        filmGenres.add(newGenre);
        filmGenresService.addFilmGenres(firstFilmId, filmGenres);

        //then
        Set<Genre> genresFirstFilmAfter = filmGenresService.getGenresByFilmId(firstFilmId);
        assertEquals(1, genresFirstFilmAfter.size());
        assertTrue(genresFirstFilmAfter.contains(newGenre));
    }

    @Test
    @DisplayName("Не должен добавить жанры фильма, когда список жанров для добавления пустой")
    void addFilmGenres_notAddingFilmGenre_listEmpty() {
        //given
        filmsDbStorage.add(firstFilm);
        Long firstFilmId = firstFilm.getId();
        Set<Genre> genresFirstFilmBefore = filmGenresService.getGenresByFilmId(firstFilmId);
        assertTrue(genresFirstFilmBefore.isEmpty());

        //when
        Set<Genre> filmGenres = new HashSet<>();
        filmGenresService.addFilmGenres(firstFilmId, filmGenres);

        //then
        Set<Genre> genresFirstFilmAfter = filmGenresService.getGenresByFilmId(firstFilmId);
        assertTrue(genresFirstFilmAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении фильму жанра, когда фильма с таким ID нет")
    void addFilmGenre_notAddingFilmGenre_filmNotExist() {
        //given
        Genre newGenre = new Genre(1, "Комедия");
        Set<Genre> filmGenres = new HashSet<>();
        filmGenres.add(newGenre);
        long filmIdNotExit = 777L;

        //when+then
        assertThrows(DataIntegrityViolationException.class, () -> filmGenresService.addFilmGenres(filmIdNotExit, filmGenres));
    }

    @Test
    @DisplayName("Должен выбросить исключение DataIntegrityViolationException при добавлении фильму жанра, когда жанра с таким ID нет")
    void addFilmGenre_notAddingFilmGenre_genreNotExist() {
        //given
        filmsDbStorage.add(firstFilm);
        Long firstFilmId = firstFilm.getId();
        Set<Genre> genresFirstFilmBefore = filmGenresService.getGenresByFilmId(firstFilmId);
        assertTrue(genresFirstFilmBefore.isEmpty());

        //when+then
        int genreIdNotExit = 777;
        Genre newGenre = new Genre(genreIdNotExit, "Комедия");
        Set<Genre> filmGenres = new HashSet<>();
        filmGenres.add(newGenre);
        assertThrows(DataIntegrityViolationException.class, () -> filmGenresService.addFilmGenres(firstFilmId, filmGenres));
    }

    @Test
    @DisplayName("Должен удалить все жанры фильма по его ID, когда они были добавлены")
    void deleteAllFilmGenresByFilmId_genresAdding() {
        //given
        Genre newGenre = new Genre(1, "Комедия");
        Set<Genre> filmGenres = new HashSet<>();
        filmGenres.add(newGenre);
        filmsDbStorage.add(firstFilm);
        Long firstFilmId = firstFilm.getId();
        filmGenresService.addFilmGenres(firstFilmId, filmGenres);
        Set<Genre> genresFirstFilmBefore = filmGenresService.getGenresByFilmId(firstFilmId);
        assertFalse(genresFirstFilmBefore.isEmpty());

        //when
        filmGenresService.deleteAllFilmGenresByFilmId(firstFilmId);

        //then
        Set<Genre> genresFirstFilmAfter = filmGenresService.getGenresByFilmId(firstFilmId);
        assertTrue(genresFirstFilmAfter.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть пустой список жанров фильма по его ID, когда жанры не были добавлены фильму")
    void getGenresByFilmId_returnEmptySet_filmHasNoGenres() {
        //given
        filmsDbStorage.add(firstFilm);
        Long firstFilmId = firstFilm.getId();

        //when
        Set<Genre> genresFirstFilm = filmGenresService.getGenresByFilmId(firstFilmId);

        //then
        assertTrue(genresFirstFilm.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть список жанров фильма по его ID, когда жанры были добавлены фильму")
    void getGenresByFilmId_returnNotEmptySet_filmHasGenres() {
        //given
        Genre newGenre = new Genre(1, "Комедия");
        Set<Genre> filmGenres = new HashSet<>();
        filmGenres.add(newGenre);
        filmsDbStorage.add(firstFilm);
        Long firstFilmId = firstFilm.getId();
        filmGenresService.addFilmGenres(firstFilmId, filmGenres);

        //when
        Set<Genre> genresFirstFilm = filmGenresService.getGenresByFilmId(firstFilmId);

        //then
        assertTrue(genresFirstFilm.contains(newGenre));
    }
}