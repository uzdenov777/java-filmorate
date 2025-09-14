package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class GenresDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private GenresDbStorage genresDbStorage;

    @BeforeEach
    void setUp() {
        genresDbStorage = new GenresDbStorage(jdbcTemplate);
    }

    @Test
    @DisplayName("Должен вернуть жанр ID:1, \"Комедия\"")
    void findById_genreComedyExist() {
        //when

        Optional<Genre> genreOpt = genresDbStorage.findById(1);
        Genre resGenre = genreOpt.get();

        //then
        int resGenreId = resGenre.getId();
        String resGenreName = resGenre.getName();
        assertEquals(1, resGenreId);
        assertEquals("Комедия", resGenreName);
    }

    @Test
    @DisplayName("Должен вернуть жанр ID:3, \"Мультфильм\"")
    void findById_genreCartoonExist() {
        //when
        Optional<Genre> genreOpt = genresDbStorage.findById(3);
        Genre resGenre = genreOpt.get();

        //then
        int resGenreId = resGenre.getId();
        String resGenreName = resGenre.getName();
        assertEquals(3, resGenreId);
        assertEquals("Мультфильм", resGenreName);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional, когда жанра по ID не найдено")
    void findById_genreNotExist() {
        //given
        int idGenreNotExits = 99;

        //when
        Optional<Genre> genreOpt = genresDbStorage.findById(idGenreNotExits);
        //then
        assertTrue(genreOpt.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть список с 6 жанрами, которые добавлены при инициализации по умолчанию")
    void findAll() {
        //when
        List<Genre> genres = genresDbStorage.findAll();

        //then
        int size = genres.size();
        assertEquals(6, size);

        Genre genreComedy = genres.get(0);
        int genreComedyId = genreComedy.getId();
        String genreComedyName = genreComedy.getName();
        assertEquals(1, genreComedyId);
        assertEquals("Комедия", genreComedyName);

        Genre genreDrama = genres.get(1);
        int genreDramaId = genreDrama.getId();
        String genreDramaName = genreDrama.getName();
        assertEquals(2, genreDramaId);
        assertEquals("Драма", genreDramaName);

        Genre genreCartoon = genres.get(2);
        int genreCartoonId = genreCartoon.getId();
        String genreCartoonName = genreCartoon.getName();
        assertEquals(3, genreCartoonId);
        assertEquals("Мультфильм", genreCartoonName);

        Genre genreThriller = genres.get(3);
        int genreThrillerId = genreThriller.getId();
        String genreThrillerName = genreThriller.getName();
        assertEquals(4, genreThrillerId);
        assertEquals("Триллер", genreThrillerName);

        Genre genreDocumentary = genres.get(4);
        int genreDocumentaryId = genreDocumentary.getId();
        String genreDocumentaryName = genreDocumentary.getName();
        assertEquals(5, genreDocumentaryId);
        assertEquals("Документальный", genreDocumentaryName);

        Genre genreAction = genres.get(5);
        int genreActionId = genreAction.getId();
        String genreActionName = genreAction.getName();
        assertEquals(6, genreActionId);
        assertEquals("Боевик", genreActionName);
    }

    @Test
    @DisplayName("Должен вернуть true, когда id существует в бд")
    void isExistsGenre_returnTrue_genreIdExists() {
        //given
        int idGenreExits = 1;

        //when
        boolean exists = genresDbStorage.isExistsGenre(idGenreExits);

        //then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Должен вернуть false, когда id не существует в бд")
    void isExistsGenre_returnFalse_genreIdNotExists() {
        //given
        int idGenreNotExits = 99;

        //when
        boolean exists = genresDbStorage.isExistsGenre(idGenreNotExits);

        //then
        assertFalse(exists);
    }
}