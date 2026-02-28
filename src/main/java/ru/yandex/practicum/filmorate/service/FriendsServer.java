package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendRepository;

import java.util.List;

@Service
public class FriendsServer {

    private final FriendRepository friendRepository;

    @Autowired
    public FriendsServer(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    public void addFriend(User user, User friend) {

        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);

        friendRepository.save(friendship);
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        friendRepository.deleteByUserIdAndFriendId(userId, friendId);
    }

    public List<User> getAllFriendsByUserId(long userId) {
        return friendRepository.findFriendsByUserId(userId);
    }

    public List<User> getMutualFriends(Long idFirstUser, Long idSecondUser) {

        List<User> mutualFriends = friendRepository.findMutualFriends(idFirstUser, idSecondUser);

        return mutualFriends;
    }
}