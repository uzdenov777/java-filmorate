package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenresDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class GenresServiceTest {
    private final JdbcTemplate jdbcTemplate;

    private GenresService genresService;

    @BeforeEach
    void setUp() {
        genresService = new GenresService(new GenresDbStorage(jdbcTemplate));
    }

    @Test
    @DisplayName("Должен вернуть жанр ID:1, \"Комедия\"")
    void getGenreById_genreComedyExist() {
        //when
        Genre resGenre = genresService.getGenreById(1);

        //then
        assertEquals(1, resGenre.getId());
        assertEquals("Комедия", resGenre.getName());
    }

    @Test
    @DisplayName("Должен вернуть жанр ID:3, \"Мультфильм\"")
    void getGenreById_genreCartoonExist() {
        //when
        Genre resGenre = genresService.getGenreById(3);

        //then
        assertEquals(3, resGenre.getId());
        assertEquals("Мультфильм", resGenre.getName());
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException")
    void getGenreById_genreNotExist() {
        int idGenreNotExits = 99;

        assertThrows(ResponseStatusException.class, () -> genresService.getGenreById(idGenreNotExits));
    }

    @Test
    @DisplayName("Должен вернуть список с 6 жанрами, которые добавлены при инициализации по умолчанию")
    void getAllGenres() {
        //when
        List<Genre> genres = genresService.getAllGenres();

        //then
        assertEquals(6, genres.size());

        Genre genreComedy = genres.get(0);
        assertEquals(1, genreComedy.getId());
        assertEquals("Комедия", genreComedy.getName());

        Genre genreDrama = genres.get(1);
        assertEquals(2, genreDrama.getId());
        assertEquals("Драма", genreDrama.getName());

        Genre genreCartoon = genres.get(2);
        assertEquals(3, genreCartoon.getId());
        assertEquals("Мультфильм", genreCartoon.getName());

        Genre genreThriller = genres.get(3);
        assertEquals(4, genreThriller.getId());
        assertEquals("Триллер", genreThriller.getName());

        Genre genreDocumentary = genres.get(4);
        assertEquals(5, genreDocumentary.getId());
        assertEquals("Документальный", genreDocumentary.getName());

        Genre genreAction = genres.get(5);
        assertEquals(6, genreAction.getId());
        assertEquals("Боевик", genreAction.getName());
    }

    @Test
    @DisplayName("Должен вернуть true, когда id существует в бд")
    void isExistsGenre_returnTrue_genreIdExists() {
        //given
        int idGenreExits = 1;

        //when
        boolean exists = genresService.isGenreExist(idGenreExits);

        //then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Должен вернуть false, когда id не существует в бд")
    void isExistsGenre_returnFalse_genreIdNotExists() {
        //given
        int idGenreNotExits = 99;

        //when+then
        assertThrows(ResponseStatusException.class, () -> genresService.isGenreExist(idGenreNotExits));
    }
}