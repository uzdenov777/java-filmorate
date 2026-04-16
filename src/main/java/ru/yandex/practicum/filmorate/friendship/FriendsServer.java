package ru.yandex.practicum.filmorate.friendship;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

        try {
            friendRepository.save(friendship);
        }catch(DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Нельзя дважды сохранить дружбу user: " + user + ", friend: " + friend);
        }
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