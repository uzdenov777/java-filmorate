package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmGenresStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.storage.dao.GenresDbStorage.getGenreRowMapper;

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
    public void deleteFilmGenresByFilmId(Long filmId) {
        String sql = "DELETE FROM FILM_GENRES WHERE film_id = ?";

        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        String query = "SELECT genres.genre_id, genres.genre_name " +
                "FROM film_genres " +
                "JOIN genres ON film_genres.genre_id = genres.genre_id " +
                "WHERE film_genres.film_id = ?";

        return jdbcTemplate.query(query, getGenreRowMapper(), filmId);
    }
}