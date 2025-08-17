package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmGenresStorage;

@Repository
public class FilmGenresDbStorage implements FilmGenresStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFilmGenre(Long filmId, Integer genreId) {
        String sql = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";

        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public void deleteFilmGenreByFilmId(Long filmId) {
        String sql = "DELETE FROM FILM_GENRES WHERE film_id = ?";

        jdbcTemplate.update(sql, filmId);
    }
}