package ru.yandex.practicum.filmorate.film;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.director.model.DirectorDto;
import ru.yandex.practicum.filmorate.film.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.genre.model.dto.GenreDto;
import ru.yandex.practicum.filmorate.mpa.Mpa;
import ru.yandex.practicum.filmorate.user.model.dto.UserDto;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.film.SortingType.LIKES;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class FilmControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private DirectorDto directorDto;

    private FilmDto filmFirst;
    private FilmDto filmSecond;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        directorDto = new DirectorDto();
        directorDto.setName("Director");

        filmFirst = new FilmDto();
        filmFirst.setName("First");
        filmFirst.setDescription("First Description");
        filmFirst.setReleaseDate(LocalDate.now().minusDays(1));
        filmFirst.setDuration(1L);
        filmFirst.setMpa(new Mpa(1L, null));
        filmFirst.setGenres(Set.of(new GenreDto(1L, null)));

        filmSecond = new FilmDto();
        filmSecond.setName("Second");
        filmSecond.setDescription("Second Description");
        filmSecond.setReleaseDate(LocalDate.now().minusDays(2));
        filmSecond.setDuration(2L);
        filmSecond.setMpa(new Mpa(2L, null));
        filmSecond.setGenres(Set.of(new GenreDto(2L, null)));

        userDto = new UserDto();
        userDto.setName("User");
        userDto.setLogin("User");
        userDto.setEmail("user@gmail.com");
        userDto.setBirthday(LocalDate.now().minusDays(1));
    }

    @SneakyThrows
    @Test
    void add_whenRequestValid_thenReturn200AndFilmDto() {
        //given
        addDirector();
        filmFirst.setDirectors(Set.of(directorDto));

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(filmFirst.getName()))
                .andExpect(jsonPath("$.description").value(filmFirst.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(filmFirst.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(filmFirst.getDuration()))
                .andExpect(jsonPath("$.mpa.id").value(filmFirst.getMpa().getId()))
                .andExpect(jsonPath("$.genres.size()").value(filmFirst.getGenres().size()))
                .andExpect(jsonPath("$.directors.size()").value(filmFirst.getDirectors().size()));
    }

    @SneakyThrows
    @Test
    void add_whenGenresIsEmpty_thenSavedAndReturn200AndFilmDto() {
        //given
        addDirector();
        filmFirst.setDirectors(Set.of(directorDto));

        filmFirst.setGenres(Set.of());

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(filmFirst.getName()))
                .andExpect(jsonPath("$.description").value(filmFirst.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(filmFirst.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(filmFirst.getDuration()))
                .andExpect(jsonPath("$.mpa.id").value(filmFirst.getMpa().getId()))
                .andExpect(jsonPath("$.genres.size()").value(filmFirst.getGenres().size()))
                .andExpect(jsonPath("$.directors.size()").value(filmFirst.getDirectors().size()));
    }

    @SneakyThrows
    @Test
    void add_whenDirectorsIsEmpty_thenSavedAndReturn200AndFilmDto() {
        //given
        filmFirst.setDirectors(Set.of());

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(filmFirst.getName()))
                .andExpect(jsonPath("$.description").value(filmFirst.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(filmFirst.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(filmFirst.getDuration()))
                .andExpect(jsonPath("$.mpa.id").value(filmFirst.getMpa().getId()))
                .andExpect(jsonPath("$.genres.size()").value(filmFirst.getGenres().size()))
                .andExpect(jsonPath("$.directors.size()").value(filmFirst.getDirectors().size()));
    }

    @SneakyThrows
    @Test
    void add_whenNameIsNull_thenReturn400() {
        //given
        filmFirst.setName(null);

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenDescriptionIsNull_thenReturn400() {
        //given
        filmFirst.setDescription(null);

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenReleaseDateIsNull_thenReturn400() {
        //given
        filmFirst.setReleaseDate(null);

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenReleaseDateIsBeforeCinemaBirth_thenReturn400() {
        //given
        filmFirst.setReleaseDate(LocalDate.of(1895, 12, 27));

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenDurationIsNull_thenReturn400() {
        //given
        filmFirst.setDuration(null);

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenDurationIsNegative_thenReturn400() {
        //given
        filmFirst.setDuration(-1L);

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenMpaIsNull_thenReturn400() {
        //given
        filmFirst.setMpa(null);

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenMpaIsNotExist_thenReturn404() {
        //given
        filmFirst.setMpa(new Mpa(777L, null));

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void add_whenGenreIsNotExist_thenReturn404() {
        //given
        filmFirst.setGenres(Set.of(new GenreDto(777L, null)));

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void add_whenDirectorIsNotExist_thenReturn404() {
        //given
        filmFirst.setDirectors(Set.of(new DirectorDto(777L, null)));

        //when+then
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void update_whenRequestValid_thenUpdateAndReturn200AndFilmDto() {
        //given
        addDirector();
        filmFirst.setDirectors(Set.of(directorDto));
        filmSecond.setDirectors(Set.of(directorDto));

        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(filmSecond.getName()))
                .andExpect(jsonPath("$.description").value(filmSecond.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(filmSecond.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(filmSecond.getDuration()))
                .andExpect(jsonPath("$.mpa.id").value(filmSecond.getMpa().getId()))
                .andExpect(jsonPath("$.genres.size()").value(filmSecond.getGenres().size()))
                .andExpect(jsonPath("$.directors.size()").value(filmSecond.getDirectors().size()));
    }

    @SneakyThrows
    @Test
    void update_whenGenresIsEmpty_thenUpdateAndReturn200AndFilmDto() {
        //given
        addDirector();

        filmFirst.setDirectors(Set.of(directorDto));
        Long filmId = addingFilm(filmFirst);

        filmSecond.setDirectors(Set.of(directorDto));
        filmSecond.setId(filmId);
        filmSecond.setGenres(Set.of());

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(filmSecond.getName()))
                .andExpect(jsonPath("$.description").value(filmSecond.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(filmSecond.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(filmSecond.getDuration()))
                .andExpect(jsonPath("$.mpa.id").value(filmSecond.getMpa().getId()))
                .andExpect(jsonPath("$.genres.size()").value(filmSecond.getGenres().size()))
                .andExpect(jsonPath("$.directors.size()").value(filmSecond.getDirectors().size()));
    }

    @SneakyThrows
    @Test
    void update_whenDirectorsIsEmpty_thenUpdateAndReturn200AndFilmDto() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(filmSecond.getName()))
                .andExpect(jsonPath("$.description").value(filmSecond.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(filmSecond.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(filmSecond.getDuration()))
                .andExpect(jsonPath("$.mpa.id").value(filmSecond.getMpa().getId()))
                .andExpect(jsonPath("$.genres.size()").value(filmSecond.getGenres().size()))
                .andExpect(jsonPath("$.directors.size()").value(filmSecond.getDirectors().size()));
    }

    @SneakyThrows
    @Test
    void update_whenNameIsNull_thenReturn400() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        filmSecond.setName(null);

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenDescriptionIsNull_thenReturn400() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        filmSecond.setDescription(null);

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenReleaseDateIsNull_thenReturn400() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        filmSecond.setReleaseDate(null);

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenReleaseDateIsBeforeCinemaBirth_thenReturn400() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        filmSecond.setReleaseDate(LocalDate.of(1895, 12, 27));

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenDurationIsNull_thenReturn400() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        filmSecond.setDuration(null);

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenDurationIsNegative_thenReturn400() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        filmSecond.setDuration(-1L);

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenMpaIsNull_thenReturn400() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        filmSecond.setMpa(null);

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenFilmIsNotExist_thenReturn404() {
        //given
        Long notExistId = 777L;
        filmSecond.setId(notExistId);

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void update_whenMpaIsNotExist_thenReturn404() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        filmSecond.setMpa(new Mpa(777L, null));

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void update_whenGenreIsNotExist_thenReturn404() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        filmSecond.setGenres(Set.of(new GenreDto(777L, null)));

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void update_whenDirectorIsNotExist_thenReturn404() {
        //given
        Long filmId = addingFilm(filmFirst);
        filmSecond.setId(filmId);

        filmSecond.setDirectors(Set.of(new DirectorDto(777L, null)));

        //when+then
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmSecond)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void deleteById_whenUserExist_thenReturn200() {
        //given
        Long filmId = addingFilm(filmFirst);

        //when+then
        mockMvc.perform(delete("/films/{filmId}", filmId))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void deleteById_whenUserNotExist_thenReturn404() {
        //given
        Long notExistId = 777L;

        //when+then
        mockMvc.perform(delete("/films/{filmId}", notExistId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getById_whenUserExist_thenReturn200AndFilmDto() {
        //given
        addDirector();

        filmFirst.setDirectors(Set.of(directorDto));
        Long filmId = addingFilm(filmFirst);

        //when+then
        mockMvc.perform(get("/films/{filmId}", filmId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(filmFirst.getName()))
                .andExpect(jsonPath("$.description").value(filmFirst.getDescription()))
                .andExpect(jsonPath("$.releaseDate").value(filmFirst.getReleaseDate().toString()))
                .andExpect(jsonPath("$.duration").value(filmFirst.getDuration()))
                .andExpect(jsonPath("$.mpa.id").value(filmFirst.getMpa().getId()))
                .andExpect(jsonPath("$.genres.size()").value(filmFirst.getGenres().size()))
                .andExpect(jsonPath("$.directors.size()").value(filmFirst.getDirectors().size()));
    }

    @SneakyThrows
    @Test
    void getById_whenUserNotExist_thenReturn400() {
        //given
        Long notExistId = 777L;

        //when+then
        mockMvc.perform(get("/films/{filmId}", notExistId))
                .andExpect(status().isNotFound());

    }

    @SneakyThrows
    @Test
    void getAllFilms_whenUserExist_thenReturn200AndListOfFilmDto() {
        //given
        addDirector();
        filmFirst.setDirectors(Set.of(directorDto));

        addingFilm(filmFirst);

        //when+then
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(filmFirst.getName()))
                .andExpect(jsonPath("$[0].description").value(filmFirst.getDescription()))
                .andExpect(jsonPath("$[0].releaseDate").value(filmFirst.getReleaseDate().toString()))
                .andExpect(jsonPath("$[0].duration").value(filmFirst.getDuration()))
                .andExpect(jsonPath("$[0].mpa.id").value(filmFirst.getMpa().getId()))
                .andExpect(jsonPath("$[0].genres.size()").value(filmFirst.getGenres().size()))
                .andExpect(jsonPath("$[0].directors.size()").value(filmFirst.getDirectors().size()));
    }

    @SneakyThrows
    @Test
    void getAllFilms_whenUserNotExist_thenReturn200AndListEmpty() {
        //when+then
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @SneakyThrows
    @Test
    void addLike_whenUserAndFilmExist_thenReturn200AndAddFilmLike() {
        //given
        Long filmId = addingFilm(filmFirst);
        Long userId = addingUser(userDto);

        //when+then
        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void addLike_whenFilmNotExist_thenReturn404() {
        //given
        Long filmId = 777L;
        Long userId = addingUser(userDto);

        //when+then
        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addLike_whenUserNotExist_thenReturn404() {
        //given
        Long filmId = addingFilm(filmFirst);
        Long userId = 777L;

        //when+then
        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void removeLike_whenUserAndFilmExist_thenReturn200AndRemoveFilmLike() {
        //given
        Long filmId = addingFilm(filmFirst);
        Long userId = addingUser(userDto);

        //when+then
        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void removeLike_whenFilmNotExist_thenReturn404() {
        //given
        Long filmId = 777L;
        Long userId = addingUser(userDto);

        //when+then
        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void removeLike_whenUserNotExist_thenReturn404() {
        //given
        Long filmId = addingFilm(filmFirst);
        Long userId = 777L;

        //when+then
        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getFilmByDirectorId_whenFilmExist_thenReturn200AndListOfFilmDto() {
        //given
        addDirector();
        Long directorId = directorDto.getId();

        filmFirst.setDirectors(Set.of(directorDto));
        addingFilm(filmFirst);

        //when+then
        mockMvc.perform(get("/films/director/{directorId}", directorId)
                        .param("sortBy", LIKES.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(filmFirst.getName()))
                .andExpect(jsonPath("$[0].description").value(filmFirst.getDescription()))
                .andExpect(jsonPath("$[0].releaseDate").value(filmFirst.getReleaseDate().toString()))
                .andExpect(jsonPath("$[0].duration").value(filmFirst.getDuration()))
                .andExpect(jsonPath("$[0].mpa.id").value(filmFirst.getMpa().getId()))
                .andExpect(jsonPath("$[0].genres.size()").value(filmFirst.getGenres().size()))
                .andExpect(jsonPath("$[0].directors.size()").value(filmFirst.getDirectors().size()));
    }

    @SneakyThrows
    @Test
    void getFilmByDirectorId_whenFilmNotExist_thenReturn200AndListEmpty() {
        //given
        addDirector();
        Long directorId = directorDto.getId();

        //when+then
        mockMvc.perform(get("/films/director/{directorId}", directorId)
                        .param("sortBy", LIKES.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @SneakyThrows
    @Test
    void getFilmByDirectorId_whenDirectorNotExist_thenReturn404() {
        //given
        Long directorId = 777L;

        //when+then
        mockMvc.perform(get("/films/director/{directorId}", directorId)
                        .param("sortBy", LIKES.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getFilmByDirectorId_whenSortingTypeNotExist_thenReturn404() {
        //given
        addDirector();
        Long directorId = directorDto.getId();

        String sortBy = "NotExist";

        //when+then
        mockMvc.perform(get("/films/director/{directorId}", directorId)
                        .param("sortBy", sortBy))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getFilmByDirectorId_whenSortingTypeEmpty_thenReturn404() {
        //given
        addDirector();
        Long directorId = directorDto.getId();

        String sortBy = "";

        //when+then
        mockMvc.perform(get("/films/director/{directorId}", directorId)
                        .param("sortBy", sortBy))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    private void addDirector() {
        String responseJson = mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(directorDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        directorDto = objectMapper.readValue(responseJson, DirectorDto.class);
    }

    @SneakyThrows
    private Long addingFilm(FilmDto filmFirst) {
        String responseJson = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmFirst)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        FilmDto resDto = objectMapper.readValue(responseJson, FilmDto.class);
        return resDto.getId();
    }

    @SneakyThrows
    private Long addingUser(UserDto userDto) {
        String responseJson = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto resDto = objectMapper.readValue(responseJson, UserDto.class);
        return resDto.getId();
    }
}