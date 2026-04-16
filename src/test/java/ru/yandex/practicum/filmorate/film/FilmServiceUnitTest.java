package ru.yandex.practicum.filmorate.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.director.DirectorService;
import ru.yandex.practicum.filmorate.director.model.DirectorDto;
import ru.yandex.practicum.filmorate.event.EventService;
import ru.yandex.practicum.filmorate.event.enums.EventType;
import ru.yandex.practicum.filmorate.event.enums.Operation;
import ru.yandex.practicum.filmorate.film.model.Film;
import ru.yandex.practicum.filmorate.film.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.filmLike.FilmLikesService;
import ru.yandex.practicum.filmorate.genre.GenresService;
import ru.yandex.practicum.filmorate.genre.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.mpa.Mpa;
import ru.yandex.practicum.filmorate.mpa.MpaService;
import ru.yandex.practicum.filmorate.user.UserService;
import ru.yandex.practicum.filmorate.user.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class FilmServiceUnitTest {
    @InjectMocks
    private FilmService filmService;

    @Mock
    private FilmsRepository filmsRepository;
    @Mock
    private UserService userService;
    @Mock
    private FilmLikesService filmLikesService;
    @Mock
    private GenresService genresService;
    @Mock
    private MpaService mpaService;
    @Mock
    private EventService eventService;
    @Mock
    private DirectorService directorService;

    private FilmMapper filmMapper;

    private FilmDto filmFirst;
    private FilmDto filmSecond;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        filmMapper = new FilmMapperImpl();
        filmService = new FilmService(
                filmsRepository,
                userService,
                filmLikesService,
                genresService,
                mpaService,
                eventService,
                directorService,
                filmMapper);

        filmFirst = new FilmDto();
        filmFirst.setName("First");
        filmFirst.setDescription("First Description");
        filmFirst.setReleaseDate(LocalDate.now().minusDays(1));
        filmFirst.setDuration(1L);
        filmFirst.setMpa(new Mpa(1L, null));
        filmFirst.setGenres(Set.of(new GenreDto(1L, null)));
        filmFirst.setDirectors(Set.of(new DirectorDto(1L, null)));

        filmSecond = new FilmDto();
        filmSecond.setName("Second");
        filmSecond.setDescription("Second Description");
        filmSecond.setReleaseDate(LocalDate.now().minusDays(2));
        filmSecond.setDuration(2L);
        filmSecond.setMpa(new Mpa(2L, null));
        filmSecond.setGenres(Set.of(new GenreDto(2L, null)));
        filmSecond.setDirectors(Set.of(new DirectorDto(2L, null)));

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void add_whenFilmValid_thenSaveFilmAndReturnFilmDto() {
        //given
        when(filmsRepository.save(any(Film.class)))
                .thenAnswer(i ->
                {
                    Film film = i.getArgument(0);

                    film.setId(1L);
                    return film;
                });

        //when
        FilmDto resDto = filmService.add(filmFirst);

        //then
        assertEquals(1L, resDto.getId());
        assertEquals(filmFirst.getName(), resDto.getName());
        assertEquals(filmFirst.getDescription(), resDto.getDescription());
        assertEquals(filmFirst.getReleaseDate(), resDto.getReleaseDate());
        assertEquals(filmFirst.getDuration(), resDto.getDuration());
        assertEquals(filmFirst.getMpa().getId(), resDto.getMpa().getId());
        assertEquals(filmFirst.getGenres(), resDto.getGenres());
        assertEquals(filmFirst.getDirectors(), resDto.getDirectors());

        verify(mpaService).checkMpaExists(anyLong());
        verify(genresService).genresExistByIds(any(Set.class));
        verify(directorService).directorsExistByIds(any(Set.class));
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void add_whenMpaNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Mpa mpa = new Mpa(777L, null);

        filmFirst.setMpa(mpa);

        Long mpaId = mpa.getId();
        ResponseStatusException expectedExc = new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден Mpa-возрастное ограничение по ID: " + mpaId);

        doThrow(expectedExc).when(mpaService).checkMpaExists(anyLong());

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.add(filmFirst));

        //then
        assertEquals(expectedExc, resExc);

        verify(mpaService).checkMpaExists(anyLong());
        verify(genresService, never()).genresExistByIds(any(Set.class));
        verify(directorService, never()).directorsExistByIds(any(Set.class));
        verify(filmsRepository, never()).save(any(Film.class));
    }

    @Test
    void add_whenReleaseDateIsBeforeCinemaBirth_thenThrowResponseStatusExceptionAndStatusBadRequest() {
        //given
        LocalDate releaseDateIsBeforeCinemaBirth = LocalDate.of(1895, 12, 26);

        filmFirst.setReleaseDate(releaseDateIsBeforeCinemaBirth);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.add(filmFirst));

        //then
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "400 BAD_REQUEST \"У фильма не правильная дата релиза: " + releaseDateIsBeforeCinemaBirth + "\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(mpaService, never()).checkMpaExists(anyLong());
        verify(genresService, never()).genresExistByIds(any(Set.class));
        verify(directorService, never()).directorsExistByIds(any(Set.class));
        verify(filmsRepository, never()).save(any(Film.class));
    }

    @Test
    void add_whenGenreNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        ResponseStatusException expectedExc = new ResponseStatusException(HttpStatus.NOT_FOUND, "Не существует один или несколько жанров");

        doThrow(expectedExc).when(genresService).genresExistByIds(filmFirst.getGenres());

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.add(filmFirst));

        //then
        assertEquals(expectedExc, resExc);

        verify(mpaService).checkMpaExists(anyLong());
        verify(genresService).genresExistByIds(any(Set.class));
        verify(directorService, never()).directorsExistByIds(any(Set.class));
        verify(filmsRepository, never()).save(any(Film.class));
    }

    @Test
    void add_whenDirectorNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        ResponseStatusException expectedExc = new ResponseStatusException(HttpStatus.NOT_FOUND, "Не существует один или несколько режиссеров");

        doThrow(expectedExc).when(directorService).directorsExistByIds(filmFirst.getDirectors());

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.add(filmFirst));

        //then
        assertEquals(expectedExc, resExc);

        verify(mpaService).checkMpaExists(anyLong());
        verify(genresService).genresExistByIds(any(Set.class));
        verify(directorService).directorsExistByIds(any(Set.class));
        verify(filmsRepository, never()).save(any(Film.class));
    }

    @Test
    void update_whenFilmValid_thenSaveFilmAndReturnFilmDto() {
        //given
        Long filmId = 1L;

        filmSecond.setId(filmId);

        when(filmsRepository.existsById(filmId)).thenReturn(true);
        doNothing().when(mpaService).checkMpaExists(anyLong());
        doNothing().when(genresService).genresExistByIds(any(Set.class));
        doNothing().when(directorService).directorsExistByIds(any(Set.class));

        when(filmsRepository.save(any(Film.class)))
                .thenAnswer(i -> i.getArgument(0));

        //when
        FilmDto resDto = filmService.update(filmSecond);

        //then
        assertEquals(filmSecond, resDto);

        verify(mpaService).checkMpaExists(anyLong());
        verify(genresService).genresExistByIds(any(Set.class));
        verify(directorService).directorsExistByIds(any(Set.class));
        verify(filmsRepository).save(any(Film.class));
    }

    @Test
    void update_whenFilmNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long filmId = 777L;

        filmSecond.setId(filmId);

        when(filmsRepository.existsById(filmId)).thenReturn(false);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.update(filmSecond));

        //then
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "404 NOT_FOUND \"Не найден фильм для обновления с ID: " + filmId + "\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(filmsRepository).existsById(filmId);
        verify(mpaService, never()).checkMpaExists(anyLong());
        verify(genresService, never()).genresExistByIds(any(Set.class));
        verify(directorService, never()).directorsExistByIds(any(Set.class));
        verify(filmsRepository, never()).save(any(Film.class));
    }

    @Test
    void update_whenMpaNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long filmId = 1L;
        Mpa notExistMpa = new Mpa(777L, null);

        filmSecond.setId(filmId);
        filmSecond.setMpa(notExistMpa);

        Long mpaId = notExistMpa.getId();
        ResponseStatusException expectedExc = new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден Mpa-возрастное ограничение по ID: " + mpaId);

        when(filmsRepository.existsById(filmId)).thenReturn(true);
        doThrow(expectedExc).when(mpaService).checkMpaExists(anyLong());

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.update(filmSecond));

        //then
        assertEquals(expectedExc, resExc);

        verify(filmsRepository).existsById(filmId);
        verify(mpaService).checkMpaExists(anyLong());
        verify(genresService, never()).genresExistByIds(any(Set.class));
        verify(directorService, never()).directorsExistByIds(any(Set.class));
        verify(filmsRepository, never()).save(any(Film.class));
    }

    @Test
    void update_whenReleaseDateIsBeforeCinemaBirth_thenThrowResponseStatusExceptionAndStatusBadRequest() {
        //given
        Long filmId = 1L;
        LocalDate releaseDateIsBeforeCinemaBirth = LocalDate.of(1895, 12, 26);

        filmSecond.setId(filmId);
        filmSecond.setReleaseDate(releaseDateIsBeforeCinemaBirth);

        when(filmsRepository.existsById(filmId)).thenReturn(true);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.update(filmSecond));

        //then
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "400 BAD_REQUEST \"У фильма не правильная дата релиза: " + releaseDateIsBeforeCinemaBirth + "\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(filmsRepository).existsById(filmId);
        verify(mpaService, never()).checkMpaExists(anyLong());
        verify(genresService, never()).genresExistByIds(any(Set.class));
        verify(directorService, never()).directorsExistByIds(any(Set.class));
        verify(filmsRepository, never()).save(any(Film.class));
    }

    @Test
    void update_whenGenreNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long filmId = 1L;
        Set<GenreDto> notExistGenre = Set.of(new GenreDto(777L, null));

        filmSecond.setId(filmId);
        filmSecond.setGenres(notExistGenre);

        ResponseStatusException expectedExc = new ResponseStatusException(HttpStatus.NOT_FOUND, "Не существует один или несколько жанров");

        when(filmsRepository.existsById(filmId)).thenReturn(true);
        doThrow(expectedExc).when(genresService).genresExistByIds(filmSecond.getGenres());

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.update(filmSecond));

        //then
        assertEquals(expectedExc, resExc);

        verify(filmsRepository).existsById(filmId);
        verify(mpaService).checkMpaExists(anyLong());
        verify(genresService).genresExistByIds(any(Set.class));
        verify(directorService, never()).directorsExistByIds(any(Set.class));
        verify(filmsRepository, never()).save(any(Film.class));
    }

    @Test
    void update_whenDirectorNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long filmId = 1L;
        Set<DirectorDto> notExistDirector = Set.of(new DirectorDto(777L, null));

        filmSecond.setId(filmId);
        filmSecond.setDirectors(notExistDirector);

        ResponseStatusException expectedExc = new ResponseStatusException(HttpStatus.NOT_FOUND, "Не существует один или несколько режиссеров");

        when(filmsRepository.existsById(filmId)).thenReturn(true);
        doThrow(expectedExc).when(directorService).directorsExistByIds(filmSecond.getDirectors());

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.update(filmSecond));

        //then
        assertEquals(expectedExc, resExc);

        verify(filmsRepository).existsById(filmId);
        verify(mpaService).checkMpaExists(anyLong());
        verify(genresService).genresExistByIds(any(Set.class));
        verify(directorService).directorsExistByIds(any(Set.class));
        verify(filmsRepository, never()).save(any(Film.class));
    }

    @Test
    void deleteFilmById_whenFilmExist_thenRemoveFilmById() {
        //given
        Long filmId = 1L;

        when(filmsRepository.existsById(filmId)).thenReturn(true);

        //when
        filmService.deleteFilmById(filmId);

        //then
        verify(filmsRepository).existsById(filmId);
        verify(filmsRepository).deleteById(filmId);
    }

    @Test
    void deleteFilmById_whenFilmNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long notExistId = 777L;

        when(filmsRepository.existsById(notExistId)).thenReturn(false);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.deleteFilmById(notExistId));

        //then
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "404 NOT_FOUND \"Не найден фильм для удаления с ID: " + notExistId + "\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(filmsRepository).existsById(notExistId);
        verify(filmsRepository, never()).deleteById(anyLong());
    }

    @Test
    void getFilmById_whenFilmExist_thenRemoveFilmById() {
        //given
        Long filmId = 1L;

        when(filmsRepository.findById(filmId))
                .thenAnswer(i ->
                {
                    filmFirst.setId(i.getArgument(0, Long.class));

                    Film film = filmMapper.toEntity(filmFirst);

                    return Optional.of(film);
                });

        //when
        FilmDto resDto = filmService.getFilmById(filmId);

        //then
        assertEquals(filmFirst, resDto);

        verify(filmsRepository).findById(filmId);
    }

    @Test
    void getFilmById_whenFilmNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long notExistId = 777L;

        when(filmsRepository.findById(notExistId)).thenReturn(Optional.empty());

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.getFilmById(notExistId));

        //then
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "404 NOT_FOUND \"Не найден фильм для возвращения по ID: " + notExistId + "\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(filmsRepository).findById(notExistId);
    }

    @Test
    void getAllFilms_whenFilmsExist_thenReturnListOfFilms() {
        //given
        when(filmsRepository.findAll(pageable))
                .thenAnswer(i ->
                {
                    Film film = filmMapper.toEntity(filmFirst);

                    return new PageImpl<>(List.of(film));
                });

        //when
        List<FilmDto> resDtos = filmService.getAllFilms(pageable);

        //then
        assertEquals(1, resDtos.size());
        assertEquals(filmFirst, resDtos.get(0));

        verify(filmsRepository).findAll(pageable);
    }

    @Test
    void getAllFilms_whenFilmsNotExist_thenReturnListEmpty() {
        //given
        when(filmsRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        //when
        List<FilmDto> resDtos = filmService.getAllFilms(pageable);

        //then
        assertEquals(0, resDtos.size());

        verify(filmsRepository).findAll(pageable);
    }

    @Test
    void getCommonLikedFilms_whenCommonFilmsThatTwoViewersEnjoyExist_thenReturnListOfFilms() {
        //given
        Long firstUserId = 1L;
        Long friendUserId = 2L;

        when(userService.isUserExists(firstUserId)).thenReturn(true);
        when(userService.isUserExists(friendUserId)).thenReturn(true);
        when(filmsRepository.findCommonLikedFilms(firstUserId, friendUserId, pageable))
                .thenAnswer(i ->
                {
                    Film film = filmMapper.toEntity(filmFirst);

                    return new PageImpl<>(List.of(film));
                });

        //when
        List<FilmDto> resFilm = filmService.getCommonLikedFilms(firstUserId, friendUserId, pageable);

        //then
        assertEquals(1, resFilm.size());
        assertEquals(filmFirst, resFilm.get(0));

        verify(userService).isUserExists(firstUserId);
        verify(userService).isUserExists(friendUserId);
        verify(filmsRepository).findCommonLikedFilms(firstUserId, friendUserId, pageable);
    }

    @Test
    void getCommonLikedFilms_whenCommonFilmsThatTwoViewersEnjoyNotExist_thenReturnListEmpty() {
        //given
        Long firstUserId = 1L;
        Long friendUserId = 2L;

        when(userService.isUserExists(firstUserId)).thenReturn(true);
        when(userService.isUserExists(friendUserId)).thenReturn(true);
        when(filmsRepository.findCommonLikedFilms(firstUserId, friendUserId, pageable)).thenReturn(new PageImpl<>(List.of()));

        //when
        List<FilmDto> resFilm = filmService.getCommonLikedFilms(firstUserId, friendUserId, pageable);

        //then
        assertEquals(0, resFilm.size());

        verify(userService).isUserExists(firstUserId);
        verify(userService).isUserExists(friendUserId);
        verify(filmsRepository).findCommonLikedFilms(firstUserId, friendUserId, pageable);
    }

    @Test
    void getCommonLikedFilms_whenUserFirstNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long firstUserId = 777L;
        Long friendUserId = 2L;

        when(userService.isUserExists(firstUserId)).thenReturn(false);
        when(userService.isUserExists(friendUserId)).thenReturn(true);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class,
                () -> filmService.getCommonLikedFilms(firstUserId, friendUserId, pageable));

        //then
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "404 NOT_FOUND \"Не найден один или два пользователя 1: " + firstUserId
                + ", 2: " + friendUserId + " при возвращении общих понравившихся фильмов\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(userService).isUserExists(firstUserId);
        verify(userService, never()).isUserExists(friendUserId);
        verify(filmsRepository, never()).findCommonLikedFilms(firstUserId, friendUserId, pageable);
    }

    @Test
    void getCommonLikedFilms_whenUserFriendNotExist_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long firstUserId = 1L;
        Long friendUserId = 777L;

        when(userService.isUserExists(firstUserId)).thenReturn(true);
        when(userService.isUserExists(friendUserId)).thenReturn(false);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class,
                () -> filmService.getCommonLikedFilms(firstUserId, friendUserId, pageable));

        //then
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "404 NOT_FOUND \"Не найден один или два пользователя 1: " + firstUserId
                + ", 2: " + friendUserId + " при возвращении общих понравившихся фильмов\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(userService).isUserExists(firstUserId);
        verify(userService).isUserExists(friendUserId);
        verify(filmsRepository, never()).findCommonLikedFilms(firstUserId, friendUserId, pageable);
    }

    @Test
    void addLikeToFilm_whenFilmAndUserExist_whenAddLikeAndSaveEvent() {
        //given
        Long filmId = 1L;
        Long userId = 2L;

        when(filmsRepository.existsById(filmId)).thenReturn(true);
        when(userService.isUserExists(userId)).thenReturn(true);
        when(filmsRepository.getReferenceById(filmId)).thenReturn(new Film());
        when(userService.getUserProxyById(userId)).thenReturn(new User());

        //when
        filmService.addLikeToFilm(filmId, userId);

        //then
        verify(filmsRepository).existsById(filmId);
        verify(userService).isUserExists(userId);
        verify(filmsRepository).getReferenceById(filmId);
        verify(userService).getUserProxyById(userId);
        verify(filmLikesService).addLikeFilm(any(Film.class), any(User.class));
        verify(eventService).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void addLikeToFilm_whenFilmNotExist_whenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long filmId = 777L;
        Long userId = 1L;

        when(filmsRepository.existsById(filmId)).thenReturn(false);
        when(userService.isUserExists(userId)).thenReturn(true);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.addLikeToFilm(filmId, userId));

        //then
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "404 NOT_FOUND \"Фильм с ID: " + filmId + " не найден для добавления или удаления ему лайка\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(userService).isUserExists(userId);
        verify(filmsRepository).existsById(filmId);
        verify(filmsRepository, never()).getReferenceById(anyLong());
        verify(userService, never()).getUserProxyById(anyLong());
        verify(filmLikesService, never()).addLikeFilm(any(Film.class), any(User.class));
        verify(eventService, never()).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void addLikeToFilm_whenUserNotExist_whenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long filmId = 1L;
        Long userId = 777L;

        when(filmsRepository.existsById(filmId)).thenReturn(true);
        when(userService.isUserExists(userId)).thenReturn(false);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.addLikeToFilm(filmId, userId));

        //then
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "404 NOT_FOUND \"Пользователь с ID: " + userId + " не найден для добавления или удаления лайка фильму: " + filmId + "\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(userService).isUserExists(userId);
        verify(filmsRepository).existsById(filmId);
        verify(filmsRepository, never()).getReferenceById(anyLong());
        verify(userService, never()).getUserProxyById(anyLong());
        verify(filmLikesService, never()).addLikeFilm(any(Film.class), any(User.class));
        verify(eventService, never()).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void deleteLikeToFilm_whenFilmAndUserExist_whenRemoveLikeAndSaveEvent() {
        //given
        Long filmId = 1L;
        Long userId = 2L;

        when(filmsRepository.existsById(filmId)).thenReturn(true);
        when(userService.isUserExists(userId)).thenReturn(true);
        when(userService.getUserProxyById(userId)).thenReturn(new User());

        //when
        filmService.deleteLikeToFilm(filmId, userId);

        //then
        verify(filmsRepository).existsById(filmId);
        verify(userService).isUserExists(userId);
        verify(userService).getUserProxyById(userId);
        verify(filmLikesService).deleteLikeFilm(filmId, userId);
        verify(eventService).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void deleteLikeToFilm_whenFilmNotExist_whenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long filmId = 777L;
        Long userId = 1L;

        when(filmsRepository.existsById(filmId)).thenReturn(false);
        when(userService.isUserExists(userId)).thenReturn(true);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.deleteLikeToFilm(filmId, userId));

        //then
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "404 NOT_FOUND \"Фильм с ID: " + filmId + " не найден для добавления или удаления ему лайка\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(userService).isUserExists(userId);
        verify(filmsRepository).existsById(filmId);
        verify(userService, never()).getUserProxyById(anyLong());
        verify(filmLikesService, never()).deleteLikeFilm(filmId, userId);
        verify(eventService, never()).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void deleteLikeToFilm_whenUserNotExist_whenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long filmId = 1L;
        Long userId = 777L;

        when(filmsRepository.existsById(filmId)).thenReturn(true);
        when(userService.isUserExists(userId)).thenReturn(false);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> filmService.deleteLikeToFilm(filmId, userId));

        //then
        HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
        String expectedMessage = "404 NOT_FOUND \"Пользователь с ID: " + userId + " не найден для добавления или удаления лайка фильму: " + filmId + "\"";

        assertEquals(expectedStatus, resExc.getStatusCode());
        assertEquals(expectedMessage, resExc.getMessage());

        verify(userService).isUserExists(userId);
        verify(filmsRepository).existsById(filmId);
        verify(userService, never()).getUserProxyById(anyLong());
        verify(filmLikesService, never()).deleteLikeFilm(filmId, userId);
        verify(eventService, never()).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void getFilmByDirectorId_whenFilmExistAndSortByYear_thenReturnListOfFilmDto() {
        //given
        Long directorId = 1L;

        doNothing().when(directorService).checkDirectorExists(directorId);
        when(filmsRepository.findByDirectorsIdOrderByReleaseDate(directorId, pageable))
                .thenAnswer(i ->
                {
                    Film film = filmMapper.toEntity(filmFirst);

                    return new PageImpl<>(List.of(film));
                });

        //when
        List<FilmDto> resDtos = filmService.getFilmsByDirectorId(directorId, "year", pageable);

        //then
        assertEquals(1, resDtos.size());
        assertEquals(filmFirst, resDtos.get(0));

        verify(directorService).checkDirectorExists(directorId);
        verify(filmsRepository).findByDirectorsIdOrderByReleaseDate(directorId, pageable);
        verify(filmsRepository, never()).findByDirectorsIdOrderByLikes(anyLong(), any(Pageable.class));
    }

    @Test
    void getFilmByDirectorId_whenFilmExistAndSortByLikes_thenReturnListOfFilmDto() {
        //given
        Long directorId = 1L;

        doNothing().when(directorService).checkDirectorExists(directorId);
        when(filmsRepository.findByDirectorsIdOrderByLikes(directorId, pageable))
                .thenAnswer(i ->
                {
                    Film film = filmMapper.toEntity(filmFirst);

                    return new PageImpl<>(List.of(film));
                });

        //when
        List<FilmDto> resDtos = filmService.getFilmsByDirectorId(directorId, "likes", pageable);

        //then
        assertEquals(1, resDtos.size());
        assertEquals(filmFirst, resDtos.get(0));

        verify(directorService).checkDirectorExists(directorId);
        verify(filmsRepository, never()).findByDirectorsIdOrderByReleaseDate(directorId, pageable);
        verify(filmsRepository).findByDirectorsIdOrderByLikes(anyLong(), any(Pageable.class));
    }

    @Test
    void getFilmByDirectorId_whenFilmNotExistAndSortByYear_thenReturnListEmpty() {
        //given
        Long directorId = 1L;

        doNothing().when(directorService).checkDirectorExists(directorId);
        when(filmsRepository.findByDirectorsIdOrderByReleaseDate(directorId, pageable)).thenReturn(new PageImpl<>(List.of()));

        //when
        List<FilmDto> resDtos = filmService.getFilmsByDirectorId(directorId, "year", pageable);

        //then
        assertEquals(0, resDtos.size());

        verify(directorService).checkDirectorExists(directorId);
        verify(filmsRepository).findByDirectorsIdOrderByReleaseDate(directorId, pageable);
        verify(filmsRepository, never()).findByDirectorsIdOrderByLikes(anyLong(), any(Pageable.class));
    }

    @Test
    void getFilmByDirectorId_whenFilmNotExistAndSortByLikes_thenReturnListOfFilmDto() {
        //given
        Long directorId = 1L;

        doNothing().when(directorService).checkDirectorExists(directorId);
        when(filmsRepository.findByDirectorsIdOrderByLikes(directorId, pageable)).thenReturn(new PageImpl<>(List.of()));

        //when
        List<FilmDto> resDtos = filmService.getFilmsByDirectorId(directorId, "likes", pageable);

        //then
        assertEquals(0, resDtos.size());

        verify(directorService).checkDirectorExists(directorId);
        verify(filmsRepository, never()).findByDirectorsIdOrderByReleaseDate(directorId, pageable);
        verify(filmsRepository).findByDirectorsIdOrderByLikes(anyLong(), any(Pageable.class));
    }

    @Test
    void getFilmByDirectorId_whenTheSortStringIsInAnyRegister_thenReturnListOfFilms() {
        Long directorId = 1L;
        String sortBy = "YeAr";

        doNothing().when(directorService).checkDirectorExists(directorId);
        when(filmsRepository.findByDirectorsIdOrderByReleaseDate(directorId, pageable)).thenReturn(new PageImpl<>(List.of()));

        //when
        List<FilmDto> resDtos = filmService.getFilmsByDirectorId(directorId, sortBy, pageable);

        //then
        assertEquals(0, resDtos.size());

        verify(directorService).checkDirectorExists(directorId);
        verify(filmsRepository).findByDirectorsIdOrderByReleaseDate(directorId, pageable);
        verify(filmsRepository, never()).findByDirectorsIdOrderByLikes(anyLong(), any(Pageable.class));
    }

    @Test
    void getFilmByDirectorId_whenFilmNotExistDirector_thenThrowResponseStatusExceptionAndStatusNotFound() {
        //given
        Long directorId = 777L;

        ResponseStatusException exception
                = new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден режиссер по ID: " + directorId);

        doThrow(exception).when(directorService).checkDirectorExists(directorId);

        //when
        ResponseStatusException resException = assertThrows(ResponseStatusException.class,
                () -> filmService.getFilmsByDirectorId(directorId, "likes", pageable));

        //then
        assertEquals(exception, resException);

        verify(directorService).checkDirectorExists(directorId);
        verify(filmsRepository, never()).findByDirectorsIdOrderByReleaseDate(directorId, pageable);
        verify(filmsRepository, never()).findByDirectorsIdOrderByLikes(anyLong(), any(Pageable.class));
    }

    @Test
    void getFilmByDirectorId_whenSortingTypeNotExist_thenThrowResponseStatusExceptionAndStatusBadRequest() {
        //given
        Long directorId = 1L;
        String sortBy = "NotExist";

        doNothing().when(directorService).checkDirectorExists(directorId);

        //when
        ResponseStatusException resException = assertThrows(ResponseStatusException.class,
                () -> filmService.getFilmsByDirectorId(directorId, sortBy, pageable));

        //then
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "400 BAD_REQUEST \"Неверный тип сортировки: " + sortBy + ". Допустимые значения: YEAR, LIKES\"";

        assertEquals(expectedStatus, resException.getStatusCode());
        assertEquals(expectedMessage, resException.getMessage());

        verify(directorService).checkDirectorExists(directorId);
        verify(filmsRepository, never()).findByDirectorsIdOrderByReleaseDate(anyLong(), any(Pageable.class));
        verify(filmsRepository, never()).findByDirectorsIdOrderByLikes(anyLong(), any(Pageable.class));
    }

    @Test
    void getFilmByDirectorId_whenSortingTypeEmpty_thenThrowResponseStatusExceptionAndStatusBadRequest() {
        //given
        Long directorId = 1L;
        String sortBy = "";

        doNothing().when(directorService).checkDirectorExists(directorId);

        //when
        ResponseStatusException resException = assertThrows(ResponseStatusException.class,
                () -> filmService.getFilmsByDirectorId(directorId, sortBy, pageable));

        //then
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedMessage = "400 BAD_REQUEST \"Передан пустой пустой SortingType\"";

        assertEquals(expectedStatus, resException.getStatusCode());
        assertEquals(expectedMessage, resException.getMessage());

        verify(directorService).checkDirectorExists(directorId);
        verify(filmsRepository, never()).findByDirectorsIdOrderByReleaseDate(anyLong(), any(Pageable.class));
        verify(filmsRepository, never()).findByDirectorsIdOrderByLikes(anyLong(), any(Pageable.class));
    }

    @Test
    void isFilmExistsById_whenFilmExist_thenReturnTrue() {
        //given
        Long filmId = 1L;

        when(filmsRepository.existsById(filmId)).thenReturn(true);

        //when
        boolean res = filmService.isFilmExistsById(filmId);

        //then
        assertTrue(res);
        verify(filmsRepository).existsById(filmId);
    }

    @Test
    void isFilmExistsById_whenFilmNotExist_thenReturnFalse() {
        //given
        Long filmId = 777L;

        when(filmsRepository.existsById(filmId)).thenReturn(false);

        //when
        boolean res = filmService.isFilmExistsById(filmId);

        //then
        assertFalse(res);
        verify(filmsRepository).existsById(filmId);
    }
}