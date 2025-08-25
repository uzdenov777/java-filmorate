package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@JdbcTest
@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private MpaDbStorage mpaDbStorage;

    @BeforeEach
    void setUp() {
        mpaDbStorage = new MpaDbStorage(jdbcTemplate);
    }

    @Test
    @DisplayName("Должен вернуть mpa ID:1, \"G\"")
    void getMpaById_mpaG_Exist() {
        //given
        int idMpaExits = 1;

        //when
        Mpa resMpa = mpaDbStorage.getMpaById(idMpaExits);

        //then
        assertEquals(1, resMpa.getId());
        assertEquals("G", resMpa.getName());
    }

    @Test
    @DisplayName("Должен вернуть mpa ID:4, \"R\"")
    void getMpaById_mpaR_Exist() {
        int idMpaExits = 4;

        //when
        Mpa resMpa = mpaDbStorage.getMpaById(idMpaExits);

        //then
        assertEquals(4, resMpa.getId());
        assertEquals("R", resMpa.getName());
    }

    @Test
    @DisplayName("Должен выбросить исключение EmptyResultDataAccessException, когда запрашиваем не существующий mpa")
    void getMpaById_mpaNotExist() {
        int idMpaNotExits = 99;

        assertThrows(EmptyResultDataAccessException.class, () -> mpaDbStorage.getMpaById(idMpaNotExits));
    }

    @Test
    @DisplayName("Должен вернуть список с 5 MPA, которые добавлены при инициализации по умолчанию")
    void getAllMpa() {
        //when
        List<Mpa> allMpa = mpaDbStorage.getAllMpa();

        //then
        assertEquals(5, allMpa.size());

        Mpa mpaG = allMpa.get(0);
        assertEquals(1, mpaG.getId());
        assertEquals("G", mpaG.getName());

        Mpa mpaPG = allMpa.get(1);
        assertEquals(2, mpaPG.getId());
        assertEquals("PG", mpaPG.getName());

        Mpa mpaPG13 = allMpa.get(2);
        assertEquals(3, mpaPG13.getId());
        assertEquals("PG-13", mpaPG13.getName());

        Mpa mpaR = allMpa.get(3);
        assertEquals(4, mpaR.getId());
        assertEquals("R", mpaR.getName());

        Mpa mpaNC17 = allMpa.get(4);
        assertEquals(5, mpaNC17.getId());
        assertEquals("NC-17", mpaNC17.getName());
    }

    @Test
    @DisplayName("Должен вернуть true, когда id существует в бд")
    void isExistsMpa_returnTrue_mpaIdExists() {
        //given
        int idMpaExits = 1;

        //when
        boolean exists = mpaDbStorage.isExistsMpa(idMpaExits);

        //then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Должен вернуть false, когда id не существует в бд")
    void isExistsMpa_returnFalse_mpaIdNotExists() {
        //given
        int idMpaNotExits = 99;

        //when
        boolean exists = mpaDbStorage.isExistsMpa(idMpaNotExits);

        //then
        assertFalse(exists);
    }
}