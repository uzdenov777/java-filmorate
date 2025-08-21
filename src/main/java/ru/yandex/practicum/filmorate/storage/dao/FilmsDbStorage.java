package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmsStorage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class FilmsDbStorage implements FilmsStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film newFilm) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Long newId = simpleJdbcInsert.executeAndReturnKey(filmsToMap(newFilm)).longValue();

        newFilm.setId(newId);
        return newFilm;
    }

    @Override
    public Film update(Film filmToUpdate) throws ResponseStatusException {
        String sql = "UPDATE films SET film_name = ?," +
                "description = ?," +
                "release_date = ?," +
                "duration = ?," +
                "mpa_id = ?" +
                "WHERE film_id = ?";

        Map<String, Object> filmToMap = filmsToMap(filmToUpdate);

        jdbcTemplate.update(sql, filmToMap.get("film_name"),
                filmToMap.get("description"),
                filmToMap.get("release_date"),
                filmToMap.get("duration"),
                filmToMap.get("mpa_id"),
                filmToMap.get("film_id")
        );

        return filmToUpdate;
    }

    @Override
    public Film getFilmById(Long filmId) {
        String sql = "SELECT * " +
                "FROM films " +
                "JOIN mpa USING (mpa_id) " +
                "WHERE film_id = ?";

        return jdbcTemplate.queryForObject(sql, getFilmRowMapper(), filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * " +
                "FROM films " +
                "JOIN mpa USING (mpa_id) " +
                "ORDER BY film_id";

        return jdbcTemplate.query(sql, getFilmRowMapper());
    }

    public boolean isFilmExists(Long filmId) throws ResponseStatusException { // Если запрос найдет такой фильм по вход. filmId,
        String sql = "SELECT EXISTS (SELECT 1 FROM films WHERE film_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, filmId);
    }

    private static Map<String, Object> filmsToMap(Film film) {
        String filmName = film.getName();
        String filmDescription = film.getDescription();
        LocalDate filmReleaseDate = film.getReleaseDate();
        Long filmDuration = film.getDuration();
        Long filmId = film.getId();
        Integer mpaId;

        if (Objects.nonNull(film.getMpa())) {
            mpaId = film.getMpa().getId();
        } else {
            mpaId = null;
        }

        HashMap<String, Object> filmMap = new HashMap<>();
        filmMap.put("film_name", filmName);
        filmMap.put("description", filmDescription);
        filmMap.put("release_date", filmReleaseDate);
        filmMap.put("duration", filmDuration);
        filmMap.put("mpa_id", mpaId);
        filmMap.put("film_id", filmId);

        return filmMap;
    }

    private static RowMapper<Film> getFilmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getLong(("duration")));
            film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            return film;
        };
    }
}