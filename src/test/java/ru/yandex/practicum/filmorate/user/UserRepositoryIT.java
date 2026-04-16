package ru.yandex.practicum.filmorate.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.film.FilmsRepository;
import ru.yandex.practicum.filmorate.film.model.Film;
import ru.yandex.practicum.filmorate.filmLike.FilmLike;
import ru.yandex.practicum.filmorate.filmLike.FilmLikesRepository;
import ru.yandex.practicum.filmorate.user.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmLikesRepository filmLikesRepository;

    @Autowired
    private FilmsRepository filmsRepository;

    private User firstUser;
    private User secondUser;

    private Film film;

    private Long firstId;
    private Long secondId;

    @BeforeEach
    void setUp() {
        firstUser = new User();
        firstUser.setName("First");
        firstUser.setEmail("firstUser@gmail.com");
        firstUser.setLogin("firstUser");
        firstUser.setBirthday(LocalDate.now().minusDays(1));
        userRepository.save(firstUser);
        firstId = firstUser.getId();

        secondUser = new User();
        secondUser.setName("secondUser");
        secondUser.setEmail("secondUser@gmail.com");
        secondUser.setLogin("secondUser");
        secondUser.setBirthday(LocalDate.now().minusDays(1));
        userRepository.save(secondUser);
        secondId = secondUser.getId();

        film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now().minusDays(1));
        film.setDuration(10L);
        filmsRepository.save(film);
    }

    @Test
    void findSimilarUserByUserId_whenSimilarUserExists_thenReturnIdSimilarUser() {
        //given
        FilmLike filmLike = new FilmLike();
        filmLike.setUser(firstUser);
        filmLike.setFilm(film);

        FilmLike filmLike2 = new FilmLike();
        filmLike2.setUser(secondUser);
        filmLike2.setFilm(film);

        filmLikesRepository.save(filmLike);
        filmLikesRepository.save(filmLike2);

        //when
        Long id = userRepository.findSimilarUserByUserId(firstId);

        //then
        assertEquals(id, secondId);
    }

    @Test
    void findSimilarUserByUserId_whenSimilarUserNotExists_thenReturnNull() {
        //given
        FilmLike filmLike = new FilmLike();
        filmLike.setUser(firstUser);
        filmLike.setFilm(film);

        filmLikesRepository.save(filmLike);

        //when
        Long id = userRepository.findSimilarUserByUserId(firstId);

        //then
        assertNull(id);
    }
}