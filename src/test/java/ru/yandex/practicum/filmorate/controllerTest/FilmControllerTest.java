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

public class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    public void setFilmController() {
        filmController = new FilmController();
        film = new Film();
        film.setName("FilmTest");
        film.setDescription("This is a test");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(1L);
    }

    @Test
    @DisplayName("Должен возвращать новый ID для фильма при каждом вызове")
    public void getFilmId() {
        int idNewOne = filmController.getNewId();
        assertEquals(1, idNewOne);

        int idNewTwo = filmController.getNewId();
        assertEquals(2, idNewTwo);

        int idNewThree = filmController.getNewId();
        assertEquals(3, idNewThree);
    }

    @Test
    @DisplayName("Должен успешно добавить фильм")
    public void add_testAddFilm() {
        filmController.add(film);

        List<Film> filmList = filmController.getAllFilms();

        assertEquals(1, filmList.size());
        assertEquals(film, filmList.get(0));
    }

    @Test
    @DisplayName("Должен успешно обновить фильм, когда фильм для обновления был ранее добавлен")
    public void update_existingFilmToUpdate() {
        filmController.add(film);

        Film newFilm = new Film();
        newFilm.setId(film.getId());
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
    public void update_notUpdatedFilm_noExistingFilmToUpdate() {
        assertThrows(ResponseStatusException.class, () -> filmController.update(film));
    }

    @Test
    @DisplayName("Должен вернуть список всех добавленных фильмов, когда фильмы добавлены")
    public void getAllFilms_getNotEmptyListAddFilms() {
        Film twoFilm = new Film();
        twoFilm.setId(filmController.getNewId());
        twoFilm.setName("New Film");
        twoFilm.setDescription("New Description");
        twoFilm.setReleaseDate(LocalDate.now());
        twoFilm.setDuration(1L);

        filmController.add(film);
        filmController.add(twoFilm);
        List<Film> filmList = filmController.getAllFilms();

        assertEquals(2, filmList.size());
        assertEquals(film, filmList.get(0));
        assertEquals(twoFilm, filmList.get(1));
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех фильмов, когда фильмы не добавлены")
    public void getAllFilms_getEmptyListAddFilms() {
        List<Film> filmList = filmController.getAllFilms();

        assertTrue(filmList.isEmpty());
    }
}
