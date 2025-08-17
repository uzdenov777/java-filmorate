package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UsersStorage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDbStorage implements UsersStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User newUser) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("USERS")
                .usingGeneratedKeyColumns("user_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(userToMap(newUser)).longValue();
        newUser.setId(id);

        return newUser;
    }

    @Override
    public User update(User userToUser) throws ResponseStatusException {
        String sql = "UPDATE USERS " +
                "SET user_name = ?," +
                "email = ?," +
                "login = ?," +
                "birthday = ? " +
                "WHERE user_id = ?";

        Map<String, Object> hashMap = userToMap(userToUser);

        jdbcTemplate.update(sql,
                hashMap.get("user_name"),
                hashMap.get("email"),
                hashMap.get("login"),
                hashMap.get("birthday"),
                userToUser.getId()
        );

        return userToUser;
    }

    @Override
    public User getUserById(long userId) throws ResponseStatusException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        return jdbcTemplate.queryForObject(sql, userRowMapper(), userId);
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";

        return jdbcTemplate.query(sql, userRowMapper());
    }

    public boolean isUserExists(Long userId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE user_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId);
    }

    private static Map<String, Object> userToMap(User user) {
        String name = user.getName();
        String email = user.getEmail();
        String login = user.getLogin();
        LocalDate birthday = user.getBirthday();

        HashMap<String, Object> map = new HashMap<>();
        map.put("user_name", name);
        map.put("email", email);
        map.put("login", login);
        map.put("birthday", birthday);

        return map;
    }

    private static RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setName(rs.getString("user_name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        };
    }
}