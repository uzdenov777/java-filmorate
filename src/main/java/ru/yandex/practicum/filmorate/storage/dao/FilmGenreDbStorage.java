package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FilmGenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFilmGenre(Long film_id, Integer genre_id) {
        String sql = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";

        jdbcTemplate.update(sql, film_id, genre_id);
    }

    public void deleteFilmGenreByFilmId(Long film_id) {
        String sql = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";

        jdbcTemplate.update(sql, film_id);
    }
}