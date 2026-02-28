//package ru.yandex.practicum.filmorate.storage.dao;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.jdbc.Sql;
//import ru.yandex.practicum.filmorate.model.Mpa;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@JdbcTest
//@Sql(scripts = {"/schema.sql", "/data.sql"}) // Инициализация БД перед тестами
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//class MpaDbStorageTest {
//    private final JdbcTemplate jdbcTemplate;
//
//    private MpaDbStorage mpaDbStorage;
//
//    @BeforeEach
//    void setUp() {
//        mpaDbStorage = new MpaDbStorage(jdbcTemplate);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть mpa ID:1, \"G\"")
//    void findById_mpaG_Exist() {
//        //given
//        int idMpaExits = 1;
//
//        //when
//        Optional<Mpa> mpaOpt = mpaDbStorage.findById(idMpaExits);
//        Mpa resMpa = mpaOpt.get();
//
//        //then
//        int mpaId = resMpa.getId();
//        String mpaName = resMpa.getName();
//        assertEquals(1, mpaId);
//        assertEquals("G", mpaName);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть mpa ID:4, \"R\"")
//    void findById_mpaR_Exist() {
//        int idMpaExits = 4;
//
//        //when
//        Optional<Mpa> mpaOpt = mpaDbStorage.findById(idMpaExits);
//        Mpa resMpa = mpaOpt.get();
//
//        //then
//        int resMpaId = resMpa.getId();
//        String resMpaName = resMpa.getName();
//        assertEquals(4, resMpaId);
//        assertEquals("R", resMpaName);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть пустой Optional, когда запрашиваем не существующий mpa с БД")
//    void findById_mpaNotExist() {
//        //given
//        int idMpaNotExits = 99;
//
//        //when
//        Optional<Mpa> mpaOpt = mpaDbStorage.findById(idMpaNotExits);
//
//        //then
//        assertTrue(mpaOpt.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Должен вернуть список с 5 MPA, которые добавлены при инициализации по умолчанию")
//    void findAll() {
//        //when
//        List<Mpa> allMpa = mpaDbStorage.findAll();
//
//        //then
//        int size = allMpa.size();
//        assertEquals(5, size);
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
//        boolean exists = mpaDbStorage.isExistsMpa(idMpaExits);
//
//        //then
//        assertTrue(exists);
//    }
//
//    @Test
//    @DisplayName("Должен вернуть false, когда id не существует в бд")
//    void isExistsMpa_returnFalse_mpaIdNotExists() {
//        //given
//        int idMpaNotExits = 99;
//
//        //when
//        boolean exists = mpaDbStorage.isExistsMpa(idMpaNotExits);
//
//        //then
//        assertFalse(exists);
//    }
//}