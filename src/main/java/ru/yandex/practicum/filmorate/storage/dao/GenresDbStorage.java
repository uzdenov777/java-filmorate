package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenresStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class GenresDbStorage implements GenresStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    static RowMapper<Genre> getGenreRowMapper() {
        return (resultSet, rowNum) -> new Genre(
                resultSet.getInt("genre_id"),
                resultSet.getString("genre_name")
        );
    }

    @Override
    public Optional<Genre> findById(int genreId) {
        try {
            Genre genre = jdbcTemplate.queryForObject("SELECT * FROM genres WHERE genre_id = ?", getGenreRowMapper(), genreId);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query("SELECT * FROM genres", getGenreRowMapper());
    }

    public boolean isExistsGenre(Integer genreId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM genres WHERE genre_id = ?)";

        return jdbcTemplate.queryForObject(sql, Boolean.class, genreId);
    }
}