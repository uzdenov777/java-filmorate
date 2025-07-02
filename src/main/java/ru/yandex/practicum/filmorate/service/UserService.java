package ru.yandex.practicum.filmorate.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Log4j2
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(long userOneId, long friendId) throws ResponseStatusException {
        checkUsersExistAndNotEqual(userOneId, friendId); // если все хорошо просто не выбросит исключение

        User userOne = userStorage.getUserById(userOneId);
        User friendUser = userStorage.getUserById(friendId);

        userOne.addFriend(friendUser);
        friendUser.addFriend(userOne);
    }

    public void deleteFriend(long idFirstUser, long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение

        User firstUser = userStorage.getUserById(idFirstUser);
        User secondUser = userStorage.getUserById(idSecondUser);

        Set<Long> friendsFirstUser = firstUser.getFriends();
        Set<Long> friendsSecondUser = secondUser.getFriends();

        friendsFirstUser.remove(idSecondUser);
        friendsSecondUser.remove(idFirstUser);
    }

    public List<User> getFriends(long userId) throws ResponseStatusException {
        User getUser = userStorage.getUserById(userId);
        if (Objects.isNull(getUser)) {
            log.error("Пользователь с ID:{} не был найден для возвращения его списка друзей", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID:" + userId + " не был найден для возвращения его списка друзей");
        }

        List<User> friends = new ArrayList<>();
        Set<Long> getUserFriendsId = getUser.getFriends();
        for (Long friendId : getUserFriendsId) {
            User user = userStorage.getUserById(friendId);
            friends.add(user);
        }
        return friends;
    }

    public List<User> getListMutualFriends(long userOneId, long friendId) throws ResponseStatusException {
        checkUsersExistAndNotEqual(userOneId, friendId);

        List<User> mutualFriends = new ArrayList<>();

        User userOne = userStorage.getUserById(userOneId);
        User friendUser = userStorage.getUserById(friendId);
        Set<Long> friendsUserOne = userOne.getFriends();
        Set<Long> friendsFriendUser = friendUser.getFriends();

        for (long idOne : friendsUserOne) {
            for (long idTwo : friendsFriendUser) {
                if (idOne == idTwo) {
                    mutualFriends.add(userStorage.getUserById(idOne));
                }
            }
        }
        return mutualFriends;
    }

    private void checkUsersExistAndNotEqual(long userOneId, long friendId) throws ResponseStatusException {
        User userOne = userStorage.getUserById(userOneId);
        User friendUser = userStorage.getUserById(friendId);

        boolean isUserOneNotExists = Objects.isNull(userOne);
        if (isUserOneNotExists) {
            log.error("Не найден пользователь для которого нужно добавить друга или удалить по ID: {}", userOneId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для которого нужно добавить или удалить друга по ID: " + userOneId);
        }

        boolean isFriendUserNotExists = Objects.isNull(friendUser);
        if (isFriendUserNotExists) {
            log.error("Не найден пользователь для добавления или удаления в друзья по ID: {}", friendId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для добавления или удаления в друзья по ID: " + friendId);
        }

        boolean isFriend = userOne.equals(friendUser);
        if (isFriend) {
            log.error("Нельзя добавить себя в друзья или удалить самого себя: {}", userOne);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя добавить себя в друзья или удалить самого себя: " + userOne);
        }
    }
}