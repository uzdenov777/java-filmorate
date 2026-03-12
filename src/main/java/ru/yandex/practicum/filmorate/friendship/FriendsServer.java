package ru.yandex.practicum.filmorate.friendship;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.user.model.User;

import java.util.Set;

@AllArgsConstructor
@Service
public class FriendsServer {

    private final FriendRepository friendRepository;

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

    public Page<User> getAllFriendsByUserId(long userId, Pageable pageable) {
        return friendRepository.findFriendsByUserId(userId, pageable);
    }

    public Page<User> getMutualFriends(Long idFirstUser, Long idSecondUser, Pageable pageable) {
        return friendRepository.findMutualFriends(idFirstUser, idSecondUser, pageable);
    }
}