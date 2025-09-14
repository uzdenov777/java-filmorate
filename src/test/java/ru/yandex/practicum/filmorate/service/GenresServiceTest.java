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
        int resGenreId = resGenre.getId();
        String resGenreName = resGenre.getName();
        assertEquals(1, resGenreId);
        assertEquals("Комедия", resGenreName);
    }

    @Test
    @DisplayName("Должен вернуть жанр ID:3, \"Мультфильм\"")
    void getGenreById_genreCartoonExist() {
        //when
        Genre resGenre = genresService.getGenreById(3);

        //then
        int resGenreId = resGenre.getId();
        String resGenreName = resGenre.getName();
        assertEquals(3, resGenreId);
        assertEquals("Мультфильм", resGenreName);
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
        int comedyId = genreComedy.getId();
        String comedyName = genreComedy.getName();
        assertEquals(1, comedyId);
        assertEquals("Комедия", comedyName);

        Genre genreDrama = genres.get(1);
        int dramaId = genreDrama.getId();
        String dramaName = genreDrama.getName();
        assertEquals(2, dramaId);
        assertEquals("Драма", dramaName);

        Genre genreCartoon = genres.get(2);
        int cartoonId = genreCartoon.getId();
        String cartoonName = genreCartoon.getName();
        assertEquals(3, cartoonId);
        assertEquals("Мультфильм", cartoonName);

        Genre genreThriller = genres.get(3);
        int thrillerId = genreThriller.getId();
        String thrillerName = genreThriller.getName();
        assertEquals(4, thrillerId);
        assertEquals("Триллер", thrillerName);

        Genre genreDocumentary = genres.get(4);
        int documentaryId = genreDocumentary.getId();
        String documentaryName = genreDocumentary.getName();
        assertEquals(5, documentaryId);
        assertEquals("Документальный", documentaryName);

        Genre genreAction = genres.get(5);
        int actionId = genreAction.getId();
        String actionName = genreAction.getName();
        assertEquals(6, actionId);
        assertEquals("Боевик", actionName);
    }

    @Test
    @DisplayName("Должен вернуть true, когда id существует в бд")
    void isGenreExist_returnTrue_genreIdExists() {
        //given
        int idGenreExits = 1;

        //when
        boolean exists = genresService.isGenreExist(idGenreExits);

        //then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Должен вернуть false, когда id не существует в бд")
    void isGenreExist_returnFalse_genreIdNotExists() {
        //given
        int idGenreNotExits = 99;

        //when+then
        assertThrows(ResponseStatusException.class, () -> genresService.isGenreExist(idGenreNotExits));
    }
}