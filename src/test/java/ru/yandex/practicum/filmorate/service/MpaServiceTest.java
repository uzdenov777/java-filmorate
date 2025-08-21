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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaServiceTest {
    private final JdbcTemplate jdbcTemplate;

    private MpaService mpaService;

    @BeforeEach
    void setUp() {
        mpaService = new MpaService(new MpaDbStorage(jdbcTemplate));
    }

    @Test
    @DisplayName("Должен вернуть mpa ID:1, \"G\"")
    void getMpaById_mpaG_Exist() {
        //given
        int idMpaExits = 1;

        //when
        Mpa resMpa = mpaService.getMpaById(idMpaExits);

        //then
        assertEquals(1, resMpa.getId());
        assertEquals("G", resMpa.getName());
    }

    @Test
    @DisplayName("Должен вернуть mpa ID:4, \"R\"")
    void getMpaById_mpaR_Exist() {
        int idMpaExits = 4;

        //when
        Mpa resMpa = mpaService.getMpaById(idMpaExits);

        //then
        assertEquals(4, resMpa.getId());
        assertEquals("R", resMpa.getName());
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда запрашиваем не существующий mpa")
    void getMpaById_mpaNotExist() {
        int idMpaNotExits = 99;

        assertThrows(ResponseStatusException.class, () -> mpaService.getMpaById(idMpaNotExits));
    }

    @Test
    @DisplayName("Должен вернуть список с 5 MPA, которые добавлены при инициализации по умолчанию")
    void getAllMpa() {
        //when
        List<Mpa> allMpa = mpaService.getAllMpa();

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
        boolean exists = mpaService.isExistsMpa(idMpaExits);

        //then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда id не существует в бд")
    void isExistsMpa_returnFalse_mpaIdNotExists() {
        //given
        int idMpaNotExits = 99;

        //when+then
        assertThrows(ResponseStatusException.class, () -> mpaService.isExistsMpa(idMpaNotExits));
    }
}