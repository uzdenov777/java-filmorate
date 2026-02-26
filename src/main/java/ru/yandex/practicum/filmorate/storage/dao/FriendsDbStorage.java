//package ru.yandex.practicum.filmorate.storage.dao;
//
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//import ru.yandex.practicum.filmorate.storage.interfaces.FriendStorage;
//
//import java.util.List;
//
//@Repository
//public class FriendsDbStorage implements FriendStorage {
//    private final JdbcTemplate jdbcTemplate;
//
//    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @Override
//    public void addFriend(Long userId, Long friendId) {
//        String sql = "INSERT INTO FRIENDS (user_id, friend_id) VALUES (?, ?)";
//
//        jdbcTemplate.update(sql, userId, friendId);
//    }
//
//    @Override
//    public void removeFriend(Long userId, Long friendId) {
//        String sql = "DELETE FROM FRIENDS WHERE user_id = ? AND friend_id = ?";
//
//        jdbcTemplate.update(sql, userId, friendId);
//    }
//
//    @Override
//    public List<Long> getFriendsIdByUserId(long userId) {
//        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
//
//        return jdbcTemplate.queryForList(sql, Long.class, userId);
//    }
//}