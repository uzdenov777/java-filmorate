package ru.yandex.practicum.filmorate.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.director.DirectorRepository;
import ru.yandex.practicum.filmorate.director.model.Director;
import ru.yandex.practicum.filmorate.film.model.Film;
import ru.yandex.practicum.filmorate.filmLike.FilmLike;
import ru.yandex.practicum.filmorate.filmLike.FilmLikesRepository;
import ru.yandex.practicum.filmorate.genre.model.Genre;
import ru.yandex.practicum.filmorate.mpa.Mpa;
import ru.yandex.practicum.filmorate.user.UserRepository;
import ru.yandex.practicum.filmorate.user.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmsRepositoryIT {

    @Autowired
    private FilmsRepository filmsRepository;

    @Autowired
    private FilmLikesRepository filmLikesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DirectorRepository directorRepository;

    private Pageable pageable;

    private Director directorFirst;
    private Director directorSecond;

    private Genre genreFirst;
    private Genre genreSecond;

    private Film filmFirst;
    private Film filmSecond;
    private Film filmThird;

    private User userFirst;
    private User userSecond;

    private Long directorFirstId;
    private Long directorSecondId;

    private Long filmFirstId;
    private Long filmSecondId;

    private Long userFirstId;
    private Long userSecondId;

    @BeforeEach
    void setUp() {
        pageable = Pageable.unpaged();

        directorFirst = new Director();
        directorFirst.setName("Director First");
        directorRepository.save(directorFirst);
        directorFirstId = directorFirst.getId();

        directorSecond = new Director();
        directorSecond.setName("Director Second");
        directorRepository.save(directorSecond);
        directorSecondId = directorSecond.getId();

        genreFirst = new Genre();
        genreFirst.setId(1L);

        genreSecond = new Genre();
        genreSecond.setId(2L);

        filmFirst = new Film();
        filmFirst.setName("First");
        filmFirst.setDescription("First Description");
        filmFirst.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmFirst.setDuration(1L);
        filmFirst.setMpa(new Mpa(1L, null));
        filmFirst.setGenres(Set.of(genreFirst));
        filmFirst.setDirectors(Set.of(directorFirst));
        filmsRepository.save(filmFirst);
        filmFirstId = filmFirst.getId();

        filmSecond = new Film();
        filmSecond.setName("Second");
        filmSecond.setDescription("Second Description");
        filmSecond.setReleaseDate(LocalDate.of(1999, 9, 9));
        filmSecond.setDuration(2L);
        filmSecond.setMpa(new Mpa(2L, null));
        filmSecond.setGenres(Set.of(genreSecond));
        filmSecond.setDirectors(Set.of(directorSecond));
        filmsRepository.save(filmSecond);
        filmSecondId = filmSecond.getId();

        filmThird = new Film();
        filmThird.setName("Third");
        filmThird.setDescription("Third Description");
        filmThird.setReleaseDate(LocalDate.now().minusDays(3));
        filmThird.setDuration(3L);
        filmThird.setMpa(new Mpa(3L, null));

        userFirst = new User();
        userFirst.setName("userFirst");
        userFirst.setLogin("userFirst");
        userFirst.setEmail("userFirst@gmail.com");
        userFirst.setBirthday(LocalDate.now().minusDays(1));
        userRepository.save(userFirst);
        userFirstId = userFirst.getId();

        userSecond = new User();
        userSecond.setName("userSecond");
        userSecond.setLogin("userSecond");
        userSecond.setEmail("second@gmail.com");
        userSecond.setBirthday(LocalDate.now().minusDays(2));
        userRepository.save(userSecond);
        userSecondId = userSecond.getId();
    }

    @Test
    void getTopPopularFilms_whenSortByLikesWithoutSpecifyingGenreAndYear_thenReturnListOfFilms() {
        //given
        FilmLike filmLike = new FilmLike();
        filmLike.setFilm(filmSecond);
        filmLike.setUser(userFirst);

        filmLikesRepository.save(filmLike);

        //when
        List<Film> films = filmsRepository.getTopPopularFilms(10, 0L, 0L);

        assertEquals(2, films.size());
        assertEquals(filmSecond, films.get(0));
        assertEquals(filmFirst, films.get(1));
    }

    @Test
    void getTopPopularFilms_whenSortByLikesWithoutSpecifyingGenreAndYearAndLikesNotExist_thenReturnListEmpty() {
        //when
        List<Film> films = filmsRepository.getTopPopularFilms(10, 0L, 0L);

        //then
        assertEquals(2, films.size());
        assertEquals(filmFirst, films.get(0));
        assertEquals(filmSecond, films.get(1));
    }

    @Test
    void getTopPopularFilms_whenSortByLikesWithGenre_thenReturnListOfFilms() {
        //given
        filmThird.setGenres(Set.of(genreFirst));
        filmsRepository.save(filmThird);

        FilmLike filmLike = new FilmLike();
        filmLike.setFilm(filmThird);
        filmLike.setUser(userFirst);
        filmLikesRepository.save(filmLike);

        //when
        List<Film> films = filmsRepository.getTopPopularFilms(10, genreFirst.getId(), 0L);

        assertEquals(2, films.size());
        assertEquals(filmThird, films.get(0));
        assertEquals(filmFirst, films.get(1));
    }

    @Test
    void getTopPopularFilms_whenSortByLikesWithGenreAndLikesNotExist_thenReturnListEmpty() {
        //given
        filmThird.setGenres(Set.of(genreFirst));
        filmsRepository.save(filmThird);

        //when
        List<Film> films = filmsRepository.getTopPopularFilms(10, genreFirst.getId(), 0L);

        //then
        assertEquals(2, films.size());
        assertEquals(filmFirst, films.get(0));
        assertEquals(filmThird, films.get(1));
    }

    @Test
    void getTopPopularFilms_whenSortByLikesWithYear_thenReturnListOfFilms() {
        //given
        filmThird.setReleaseDate(filmFirst.getReleaseDate());
        filmsRepository.save(filmThird);

        FilmLike filmLike = new FilmLike();
        filmLike.setFilm(filmThird);
        filmLike.setUser(userFirst);
        filmLikesRepository.save(filmLike);

        long year = filmFirst.getReleaseDate().getYear();

        //when
        List<Film> films = filmsRepository.getTopPopularFilms(10, 0L, year);

        assertEquals(2, films.size());
        assertEquals(filmThird, films.get(0));
        assertEquals(filmFirst, films.get(1));
    }

    @Test
    void getTopPopularFilms_whenSortByLikesWithYearAndLikesNotExist_thenReturnListEmpty() {
        //given
        filmThird.setReleaseDate(filmFirst.getReleaseDate());
        filmsRepository.save(filmThird);

        long year = filmFirst.getReleaseDate().getYear();

        //when
        List<Film> films = filmsRepository.getTopPopularFilms(10, 0L, year);

        //then
        assertEquals(2, films.size());
        assertEquals(filmFirst, films.get(0));
        assertEquals(filmThird, films.get(1));
    }

    @Test
    void getTopPopularFilms_whenSortByLikesWithGenreAndYear_thenReturnListOfFilms() {
        //given
        filmThird.setReleaseDate(filmFirst.getReleaseDate());
        filmThird.setGenres(Set.of(genreFirst));
        filmsRepository.save(filmThird);

        FilmLike filmLike = new FilmLike();
        filmLike.setFilm(filmThird);
        filmLike.setUser(userFirst);
        filmLikesRepository.save(filmLike);

        long year = filmFirst.getReleaseDate().getYear();
        long genreId = genreFirst.getId();

        //when
        List<Film> films = filmsRepository.getTopPopularFilms(10, genreId, year);

        assertEquals(2, films.size());
        assertEquals(filmThird, films.get(0));
        assertEquals(filmFirst, films.get(1));
    }

    @Test
    void getTopPopularFilms_whenSortByLikesWithGenreAndYearButAreNoLikes_thenReturnListEmpty() {
        //given
        filmThird.setReleaseDate(filmFirst.getReleaseDate());
        filmThird.setGenres(Set.of(genreFirst));
        filmsRepository.save(filmThird);

        long year = filmFirst.getReleaseDate().getYear();
        long genreId = genreFirst.getId();

        //when
        List<Film> films = filmsRepository.getTopPopularFilms(10, genreId, year);

        //then
        assertEquals(2, films.size());
        assertEquals(filmFirst, films.get(0));
        assertEquals(filmThird, films.get(1));
    }

    @Test
    void findCommonLikedFilms_whenCommonLikeExist_thenReturnListOfFilms() {
        //given
        FilmLike filmLike = new FilmLike();
        filmLike.setFilm(filmFirst);
        filmLike.setUser(userFirst);

        //лайки общему фильму
        FilmLike likeCommonFirst = new FilmLike();
        likeCommonFirst.setFilm(filmSecond);
        likeCommonFirst.setUser(userFirst);

        FilmLike likeCommonSecond = new FilmLike();
        likeCommonSecond.setFilm(filmSecond);
        likeCommonSecond.setUser(userSecond);

        filmLikesRepository.save(filmLike);
        filmLikesRepository.save(likeCommonFirst);
        filmLikesRepository.save(likeCommonSecond);

        //when
        Page<Film> filmsPage = filmsRepository.findCommonLikedFilms(userFirstId, userSecondId, pageable);

        //then
        List<Film> films = filmsPage.getContent();

        assertEquals(1, films.size());
        assertEquals(filmSecond, films.get(0));
    }

    @Test
    void findCommonLikedFilms_whenCommonLikeNotExist_thenReturnListEmpty() {
        //given
        FilmLike filmLikeFirst = new FilmLike();
        filmLikeFirst.setFilm(filmFirst);
        filmLikeFirst.setUser(userFirst);

        FilmLike filmLikeSecond = new FilmLike();
        filmLikeSecond.setFilm(filmSecond);
        filmLikeSecond.setUser(userSecond);

        filmLikesRepository.save(filmLikeFirst);
        filmLikesRepository.save(filmLikeSecond);

        //when
        Page<Film> filmsPage = filmsRepository.findCommonLikedFilms(userFirstId, userSecondId, pageable);

        //then
        List<Film> films = filmsPage.getContent();
        assertEquals(0, films.size());
    }


    @Test
    void findByDirectorsIdOrderByReleaseDate_whenFilmExist_thenReturnListOfFilms() {
        //given
        filmThird.setReleaseDate(filmFirst.getReleaseDate().minusYears(1));
        filmThird.setDirectors(Set.of(directorFirst, directorSecond));
        filmsRepository.save(filmThird);

        //when
        Page<Film> filmsPage = filmsRepository.findByDirectorsIdOrderByReleaseDate(directorFirst.getId(), pageable);

        //then
        List<Film> films = filmsPage.getContent();

        assertEquals(2, films.size());
        assertEquals(filmThird, films.get(0));
        assertEquals(filmFirst, films.get(1));
    }

    @Test
    void findByDirectorsIdOrderByReleaseDate_whenFilmNotExistByDirectorId_thenReturnListOfFilms() {
        //given
        Director director = new Director();
        director.setName("Director");
        directorRepository.save(director);

        Long directorId = director.getId();

        //when
        Page<Film> filmsPage = filmsRepository.findByDirectorsIdOrderByReleaseDate(directorId, pageable);

        //then
        List<Film> films = filmsPage.getContent();

        assertEquals(0, films.size());
    }

    @Test
    void findByDirectorsIdOrderByLikes_whenFilmExist_thenReturnListOfFilms() {
        //given
        filmThird.setDirectors(Set.of(directorFirst, directorSecond));
        filmsRepository.save(filmThird);

        FilmLike filmLike = new FilmLike();
        filmLike.setFilm(filmThird);
        filmLike.setUser(userFirst);
        filmLikesRepository.save(filmLike);

        //when
        Page<Film> filmsPage = filmsRepository.findByDirectorsIdOrderByLikes(directorFirstId, pageable);

        //then
        List<Film> films = filmsPage.getContent();

        assertEquals(2, films.size());
        assertEquals(filmThird, films.get(0));
        assertEquals(filmFirst, films.get(1));
    }

    @Test
    void findByDirectorsIdOrderByLikes_whenFilmNotExistByDirectorId_thenReturnListOfFilms() {
        //given
        Director director = new Director();
        director.setName("Director");
        directorRepository.save(director);

        Long directorId = director.getId();

        //when
        Page<Film> filmsPage = filmsRepository.findByDirectorsIdOrderByLikes(directorId, pageable);

        //then
        List<Film> films = filmsPage.getContent();

        assertEquals(0, films.size());
    }

    @Test
    void findRecommendations_whenExistFilmRecommendations_thenReturnListOfFilmRecommendations() {
        //given
        FilmLike commonLikeFirst = new FilmLike();
        commonLikeFirst.setFilm(filmFirst);
        commonLikeFirst.setUser(userFirst);

        FilmLike commonLikeSecond = new FilmLike();
        commonLikeSecond.setFilm(filmFirst);
        commonLikeSecond.setUser(userSecond);

        FilmLike filmLike = new FilmLike();
        filmLike.setFilm(filmSecond);
        filmLike.setUser(userSecond);

        filmLikesRepository.save(commonLikeFirst);
        filmLikesRepository.save(commonLikeSecond);
        filmLikesRepository.save(filmLike);

        //when
        List<Film> films = filmsRepository.findRecommendations(userFirstId, userSecondId);

        //then
        assertEquals(1, films.size());
        assertEquals(filmSecond, films.get(0));
    }

    @Test
    void findRecommendations_whenNotExistFilmRecommendations_thenReturnListEmpty() {
        //given
        FilmLike commonLikeFirst = new FilmLike();
        commonLikeFirst.setFilm(filmFirst);
        commonLikeFirst.setUser(userFirst);

        FilmLike commonLikeSecond = new FilmLike();
        commonLikeSecond.setFilm(filmFirst);
        commonLikeSecond.setUser(userSecond);

        filmLikesRepository.save(commonLikeFirst);
        filmLikesRepository.save(commonLikeSecond);

        //when
        List<Film> films = filmsRepository.findRecommendations(userFirstId, userSecondId);

        //then
        assertEquals(0, films.size());
    }
}