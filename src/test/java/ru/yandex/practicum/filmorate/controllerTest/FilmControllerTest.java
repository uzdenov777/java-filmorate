package ru.yandex.practicum.filmorate.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController;
    Film filmOne;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        filmOne = new Film();
        filmOne.setName("FilmTest");
        filmOne.setDescription("This is a test");
        filmOne.setReleaseDate(LocalDate.now());
        filmOne.setDuration(1L);
    }

    @Test
    @DisplayName("Должен устанавливать новый ID для фильма при каждом добавлении фильма")
    void getFilmId() {
        Film newFilm = new Film();
        newFilm.setName("FilmTest");
        newFilm.setDescription("This is a test");
        newFilm.setReleaseDate(LocalDate.now());
        newFilm.setDuration(1L);
        assertEquals(0, filmOne.getId());
        assertEquals(0, newFilm.getId());

        filmController.add(filmOne);
        filmController.add(newFilm);

        int idFilm = filmOne.getId();
        assertEquals(1, idFilm);
        int idNewFilm = newFilm.getId();
        assertEquals(2, idNewFilm);
    }

    @Test
    @DisplayName("Должен успешно добавить фильм")
    void add_testAddFilm() {
        List<Film> before = filmController.getAllFilms();
        assertTrue(before.isEmpty());

        filmController.add(filmOne);

        List<Film> filmList = filmController.getAllFilms();
        assertEquals(1, filmList.size());
        assertEquals(filmOne, filmList.get(0));
    }

    @Test
    @DisplayName("Должен успешно обновить фильм, когда фильм для обновления был ранее добавлен")
    void update_existingFilmToUpdate() {
        filmController.add(filmOne);
        List<Film> before = filmController.getAllFilms();
        assertEquals(1, before.size());
        assertEquals(filmOne, before.get(0));

        Film newFilm = new Film();
        newFilm.setId(filmOne.getId());
        newFilm.setName("New Film");
        newFilm.setDescription("New Description");
        newFilm.setReleaseDate(LocalDate.now());
        newFilm.setDuration(1L);
        filmController.update(newFilm);

        List<Film> filmList = filmController.getAllFilms();
        assertEquals(1, filmList.size());
        assertEquals(newFilm, filmList.get(0));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда фильма для обновления с таким ID нету")
    void update_notUpdatedFilm_noExistingFilmToUpdate() {
        assertThrows(ResponseStatusException.class, () -> filmController.update(filmOne));
    }

    @Test
    @DisplayName("Должен вернуть список всех добавленных фильмов, когда фильмы добавлены")
    void getAllFilms_getNotEmptyListAddFilms() {
        Film twoFilm = new Film();
        twoFilm.setName("New Film");
        twoFilm.setDescription("New Description");
        twoFilm.setReleaseDate(LocalDate.now());
        twoFilm.setDuration(1L);

        filmController.add(filmOne);
        filmController.add(twoFilm);

        List<Film> filmList = filmController.getAllFilms();
        assertEquals(2, filmList.size());
        assertEquals(filmOne, filmList.get(0));
        assertEquals(twoFilm, filmList.get(1));
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех фильмов, когда фильмы не добавлены")
    void getAllFilms_getEmptyListAddFilms() {
        List<Film> filmList = filmController.getAllFilms();

        assertTrue(filmList.isEmpty());
    }

    @Test
    @DisplayName("Выбросит исключение ResponseStatusException, когда releaseDate раньше или ровно 1895-12-28")
    void setReleaseDateEarlier1895_12_28() {
        filmOne.setId(1);
        filmOne.setName("FilmTest");
        filmOne.setDuration(20L);
        filmOne.setDescription("This is a test");

        LocalDate invalidReleaseDate = LocalDate.of(1895, 12, 28);
        filmOne.setReleaseDate(invalidReleaseDate);
        assertThrows(ResponseStatusException.class, () -> filmController.add(filmOne));

        LocalDate releaseDateBefore1895 = LocalDate.of(1895, 12, 27);
        filmOne.setReleaseDate(releaseDateBefore1895);
        assertThrows(ResponseStatusException.class, () -> filmController.add(filmOne));
    }
}
