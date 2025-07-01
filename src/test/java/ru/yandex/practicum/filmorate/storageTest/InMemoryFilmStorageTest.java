package ru.yandex.practicum.filmorate.storageTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryFilmStorageTest {
    InMemoryFilmStorage filmStorage;
    Film firstFilm;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        firstFilm = new Film();
        firstFilm.setName("FilmTest");
        firstFilm.setDescription("This is a test");
        firstFilm.setReleaseDate(LocalDate.now());
        firstFilm.setDuration(1L);
    }

    @Test
    @DisplayName("Выбросит исключение ResponseStatusException, когда releaseDate раньше или ровно 1895-12-28")
    void setReleaseDateEarlier1895_12_28() {
        //given
        firstFilm.setId(1);
        firstFilm.setName("FilmTest");
        firstFilm.setDuration(20L);
        firstFilm.setDescription("This is a test");

        //when+then
        LocalDate invalidReleaseDate = LocalDate.of(1895, 12, 28);
        firstFilm.setReleaseDate(invalidReleaseDate);
        assertThrows(ResponseStatusException.class, () -> filmStorage.add(firstFilm));

        //when+then
        LocalDate releaseDateBefore1895 = LocalDate.of(1895, 12, 27);
        firstFilm.setReleaseDate(releaseDateBefore1895);
        assertThrows(ResponseStatusException.class, () -> filmStorage.add(firstFilm));
    }
}
