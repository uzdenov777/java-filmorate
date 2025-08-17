package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.List;

public interface FriendsStorage {

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<Long> getAllFriendsIdByUserId(long userId);
}