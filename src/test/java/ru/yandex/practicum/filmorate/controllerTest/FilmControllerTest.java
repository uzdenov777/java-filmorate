//package ru.yandex.practicum.filmorate.controllerTest;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.web.server.ResponseStatusException;
//import ru.yandex.practicum.filmorate.controller.FilmController;
//import ru.yandex.practicum.filmorate.model.Film;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FilmControllerTest {
//    FilmController filmController;
//    Film firstFilm;
//
//    @BeforeEach
//    void setUp() {
//        filmController = new FilmController();
//        firstFilm = new Film();
//        firstFilm.setName("FilmTest");
//        firstFilm.setDescription("This is a test");
//        firstFilm.setReleaseDate(LocalDate.now());
//        firstFilm.setDuration(1L);
//    }
//
//    @Test
////    @DisplayName("Должен устанавливать новый ID для фильма при каждом добавлении фильма")
//    void getFilmId()
//        //given
//        Film newFilm = new Film();
//        newFilm.setName("FilmTest");
//        newFilm.setDescription("This is a test");
//        newFilm.setReleaseDate(LocalDate.now());
//        newFilm.setDuration(1L);
//        assertEquals(0, firstFilm.getId());
//        assertEquals(0, newFilm.getId());
//
//        //when
//        filmController.add(firstFilm);
//        filmController.add(newFilm);
//
//        //then
//        int idFilm = firstFilm.getId();
//        assertEquals(1, idFilm);
//        int idNewFilm = newFilm.getId();
//        assertEquals(2, idNewFilm);
//    }
//
//
//

//
//    @Test
//    @DisplayName("Выбросит исключение ResponseStatusException, когда releaseDate раньше или ровно 1895-12-28")
//    void setReleaseDateEarlier1895_12_28() {
//        //given
//        firstFilm.setId(1);
//        firstFilm.setName("FilmTest");
//        firstFilm.setDuration(20L);
//        firstFilm.setDescription("This is a test");
//
//        //when+then
//        LocalDate invalidReleaseDate = LocalDate.of(1895, 12, 28);
//        firstFilm.setReleaseDate(invalidReleaseDate);
//        assertThrows(ResponseStatusException.class, () -> filmController.add(firstFilm));
//
//        //when+then
//        LocalDate releaseDateBefore1895 = LocalDate.of(1895, 12, 27);
//        firstFilm.setReleaseDate(releaseDateBefore1895);
//        assertThrows(ResponseStatusException.class, () -> filmController.add(firstFilm));
//    }
//}
