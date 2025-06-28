package ru.yandex.practicum.filmorate.serviceTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    FilmController filmController;
    Film firstFilm;

    @Test
    @DisplayName("Должен успешно добавить фильм")
    void add_testAddFilm() {
        //given
        List<Film> before = filmController.getAllFilms();
        assertTrue(before.isEmpty());

        //when
        filmController.add(firstFilm);

        //then
        List<Film> filmList = filmController.getAllFilms();
        assertEquals(1, filmList.size());
        assertEquals(firstFilm, filmList.get(0));
    }

    @Test
    @DisplayName("Должен успешно обновить фильм, когда фильм для обновления был ранее добавлен")
    void update_existingFilmToUpdate() {
        //given
        filmController.add(firstFilm);
        List<Film> before = filmController.getAllFilms();
        assertEquals(1, before.size());
        assertEquals(firstFilm, before.get(0));

        //when
        Film newFilm = new Film();
        newFilm.setId(firstFilm.getId());
        newFilm.setName("New Film");
        newFilm.setDescription("New Description");
        newFilm.setReleaseDate(LocalDate.now());
        newFilm.setDuration(1L);
        filmController.update(newFilm);

        //then
        List<Film> filmList = filmController.getAllFilms();
        assertEquals(1, filmList.size());
        assertEquals(newFilm, filmList.get(0));
    }

    @Test
    @DisplayName("Должен выбросить исключение ResponseStatusException, когда фильма для обновления с таким ID нету")
    void update_notUpdatedFilm_noExistingFilmToUpdate() {
        //when+then
        assertThrows(ResponseStatusException.class, () -> filmController.update(firstFilm));
    }

    @Test
    @DisplayName("Должен вернуть список всех добавленных фильмов, когда фильмы добавлены")
    void getAllFilms_getNotEmptyListAddFilms() {
        //given
        Film secondFilm = new Film();
        secondFilm.setName("New Film");
        secondFilm.setDescription("New Description");
        secondFilm.setReleaseDate(LocalDate.now());
        secondFilm.setDuration(1L);

        //when
        filmController.add(firstFilm);
        filmController.add(secondFilm);

        //then
        List<Film> filmList = filmController.getAllFilms();
        assertEquals(2, filmList.size());
        assertEquals(firstFilm, filmList.get(0));
        assertEquals(secondFilm, filmList.get(1));
    }

    @Test
    @DisplayName("Должен вернуть пустой список всех фильмов, когда фильмы не добавлены")
    void getAllFilms_getEmptyListAddFilms() {
        List<Film> filmList = filmController.getAllFilms();
        assertTrue(filmList.isEmpty());
    }
}
