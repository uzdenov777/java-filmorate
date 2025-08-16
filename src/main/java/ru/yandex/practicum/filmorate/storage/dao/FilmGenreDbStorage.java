package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FilmGenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFilmGenre(Long filmId, Integer genreId) {
        String sql = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";

        jdbcTemplate.update(sql, filmId, genreId);
    }

    public void deleteFilmGenreByFilmId(Long filmId) {
        String sql = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";

        jdbcTemplate.update(sql, filmId);
    }
}