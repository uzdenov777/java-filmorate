package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.dao.FriendsDbStorage;

import java.util.List;

@Service
public class FriendsServer {
    FriendsDbStorage friendsDbStorage;

    @Autowired
    public FriendsServer(FriendsDbStorage friendsDbStorage) {
        this.friendsDbStorage = friendsDbStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        friendsDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        friendsDbStorage.removeFriend(userId, friendId);
    }

    public List<Long> getAllFriendsIdByUserId(long userId) {
        return friendsDbStorage.getAllFriendsIdByUserId(userId);
    }
}