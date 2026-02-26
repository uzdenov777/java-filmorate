//package ru.yandex.practicum.filmorate.service;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.web.server.ResponseStatusException;
//import ru.yandex.practicum.filmorate.model.Mpa;
//import ru.yandex.practicum.filmorate.storage.dao.MpaDbStorage;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@JdbcTest
//@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//class MpaServiceTest {
//    private final JdbcTemplate jdbcTemplate;
//
//    private MpaService mpaService;
//
//    @BeforeEach
//    void setUp() {
//        mpaService = new MpaService(new MpaDbStorage(jdbcTemplate));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть mpa ID:1, \"G\"")
//    void getMpaById_mpaG_Exist() {
//        //given
//        int idMpaExits = 1;
//
//        //when
//        Mpa resMpa = mpaService.getMpaById(idMpaExits);
//
//        //then
//        int resMpaId = resMpa.getId();
//        String resMpaName = resMpa.getName();
//        assertEquals(1, resMpaId);
//        assertEquals("G", resMpaName);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть mpa ID:4, \"R\"")
//    void getMpaById_mpaR_Exist() {
//        int idMpaExits = 4;
//
//        //when
//        Mpa resMpa = mpaService.getMpaById(idMpaExits);
//
//        //then
//        int resMpaId = resMpa.getId();
//        String resMpaName = resMpa.getName();
//        assertEquals(4, resMpaId);
//        assertEquals("R", resMpaName);
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда запрашиваем не существующий mpa")
//    void getMpaById_mpaNotExist() {
//        int idMpaNotExits = 99;
//
//        assertThrows(ResponseStatusException.class, () -> mpaService.getMpaById(idMpaNotExits));
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список с 5 MPA, которые добавлены при инициализации по умолчанию")
//    void getAllMpa() {
//        //when
//        List<Mpa> allMpa = mpaService.getAllMpa();
//
//        //then
//        assertEquals(5, allMpa.size());
//
//        Mpa mpaG = allMpa.get(0);
//        int mpaGId = mpaG.getId();
//        String mpaGName = mpaG.getName();
//        assertEquals(1, mpaGId);
//        assertEquals("G", mpaGName);
//
//        Mpa mpaPG = allMpa.get(1);
//        int mpaPGId = mpaPG.getId();
//        String mpaPGName = mpaPG.getName();
//        assertEquals(2, mpaPGId);
//        assertEquals("PG", mpaPGName);
//
//        Mpa mpaPG13 = allMpa.get(2);
//        int mpaPG13Id = mpaPG13.getId();
//        String mpaPG13Name = mpaPG13.getName();
//        assertEquals(3, mpaPG13Id);
//        assertEquals("PG-13", mpaPG13Name);
//
//        Mpa mpaR = allMpa.get(3);
//        int mpaRId = mpaR.getId();
//        String mpaRName = mpaR.getName();
//        assertEquals(4, mpaRId);
//        assertEquals("R", mpaRName);
//
//        Mpa mpaNC17 = allMpa.get(4);
//        int mpaNC17Id = mpaNC17.getId();
//        String mpaNC17Name = mpaNC17.getName();
//        assertEquals(5, mpaNC17Id);
//        assertEquals("NC-17", mpaNC17Name);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть true, когда id существует в бд")
//    void isExistsMpa_returnTrue_mpaIdExists() {
//        //given
//        int idMpaExits = 1;
//
//        //when
//        boolean exists = mpaService.isExistsMpa(idMpaExits);
//
//        //then
//        assertTrue(exists);
//    }
//
//    @Test
//    @DisplayName("Должен выбросить исключение ResponseStatusException, когда id не существует в бд")
//    void isExistsMpa_returnFalse_mpaIdNotExists() {
//        //given
//        int idMpaNotExits = 99;
//
//        //when+then
//        assertThrows(ResponseStatusException.class, () -> mpaService.isExistsMpa(idMpaNotExits));
//    }
//}