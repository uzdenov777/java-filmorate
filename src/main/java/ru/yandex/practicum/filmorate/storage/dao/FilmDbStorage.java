package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(filmsToMap(film)).longValue();
        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET film_name = ?," +
                "description = ?," +
                "release_date = ?," +
                "duration = ?," +
                "mpa_id = ?" +
                "WHERE film_id = ?";

        jdbcTemplate.update(sql, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaId(),
                film.getId());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return List.of();
    }

    @Override
    public Film getFilmById(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM films WHERE film_id = ?", getFilmRowMapper(), id);
    }

    private static Map<String, Object> filmsToMap(Film film) {
        return Map.of(
                "film_name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa_id", film.getMpaId()
        );
    }

    private static RowMapper<Film> getFilmRowMapper() {
        return (rs, rowNum) -> new Film(
                rs.getLong("film_id"),
                rs.getString("film_name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration"),
                rs.getInt("mpa_id")
        );
    }
}