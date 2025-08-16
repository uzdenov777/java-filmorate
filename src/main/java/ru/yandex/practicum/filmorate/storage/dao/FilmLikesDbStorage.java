package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FilmLikesDbStorage {
    JdbcTemplate jdbcTemplate;

    public FilmLikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLikeFilm(long filmId, long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";

        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLikeFilm(long filmId, long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sql, filmId, userId);
    }

    public void deleteAllFilmLikesByFilmId(long filmId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ?";

        jdbcTemplate.update(sql, filmId);
    }

    public List<Long> getFilmLikesByFilmId(long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";

        return jdbcTemplate.queryForList(sql, Long.class, filmId);
    }
}