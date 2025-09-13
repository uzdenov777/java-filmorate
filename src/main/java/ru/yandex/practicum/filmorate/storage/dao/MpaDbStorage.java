package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static RowMapper<Mpa> mpaRowMapper() {
        return (rs, rowNum) -> new Mpa(
                rs.getInt("id"),
                rs.getString("mpa_name")
        );
    }

    @Override
    public Optional<Mpa> findById(int mpaId) {
        try {
            Mpa mpa = jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE id = ?", mpaRowMapper(), mpaId);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> findAll() {
        return jdbcTemplate.query("SELECT * FROM mpa", mpaRowMapper());
    }

    public boolean isExistsMpa(Integer mpaId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM mpa WHERE id = ?)";

        return jdbcTemplate.queryForObject(sql, Boolean.class, mpaId);
    }
}