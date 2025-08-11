package ru.yandex.practicum.filmorate.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Log4j2
@Service
public class UserService {
    private final UserStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public User add(User user) {
        return userDbStorage.add(user);
    }

    public User update(User user) {
        return userDbStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public void addFriend(long idFirstUser, long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение

        User firstUser = userDbStorage.getUserById(idFirstUser);
        User secondUser = userDbStorage.getUserById(idSecondUser);

        Set<Long> friendsFirstUser = firstUser.getFriends();
        Set<Long> friendsSecondUser = secondUser.getFriends();

        friendsFirstUser.add(idSecondUser);
        friendsSecondUser.add(idFirstUser);
    }

    public void deleteFriend(long idFirstUser, long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение

        User firstUser = userDbStorage.getUserById(idFirstUser);
        User secondUser = userDbStorage.getUserById(idSecondUser);

        Set<Long> friendsFirstUser = firstUser.getFriends();
        Set<Long> friendsSecondUser = secondUser.getFriends();

        friendsFirstUser.remove(idSecondUser);
        friendsSecondUser.remove(idFirstUser);
    }

    public User getUserById(long userId) throws ResponseStatusException {
        Optional<User> userFirst = Optional.ofNullable(userDbStorage.getUserById(userId));
        if (userFirst.isEmpty()) {
            log.error("Пользователь с ID:{} не был найден", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID:" + userId + " не был найден");
        }
        return userFirst.get();
    }

    public List<User> getFriends(long userId) throws ResponseStatusException {
        Optional<User> userFirst = Optional.ofNullable(userDbStorage.getUserById(userId));
        if (userFirst.isEmpty()) {
            log.error("Пользователь с ID:{} не был найден для возвращения его списка друзей", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID:" + userId + " не был найден для возвращения его списка друзей");
        }

        List<User> friends = new ArrayList<>();
        Set<Long> getUserFriendsId = userFirst.get().getFriends();
        for (Long friendId : getUserFriendsId) {
            Optional<User> user = Optional.ofNullable(userDbStorage.getUserById(friendId));
            user.ifPresent(friends::add);
        }
        return friends;
    }

    public List<User> getListMutualFriends(long idUserFirst, long idUserSecond) throws ResponseStatusException {
        checkUsersExistAndNotEqual(idUserFirst, idUserSecond);

        User userFirst = userDbStorage.getUserById(idUserFirst);
        User userSecond = userDbStorage.getUserById(idUserSecond);

        Set<Long> friendsUserFirst = new HashSet<>(userFirst.getFriends());
        Set<Long> friendsUserSecond = userSecond.getFriends();
        friendsUserFirst.retainAll(friendsUserSecond);

        List<User> mutualFriends = new ArrayList<>();
        for (long id : friendsUserFirst) {
            mutualFriends.add(userDbStorage.getUserById(id));
        }
        return mutualFriends;
    }

    private void checkUsersExistAndNotEqual(long idUserFirst, long idUserSecond) throws ResponseStatusException {
        Optional<User> userFirst = Optional.ofNullable(userDbStorage.getUserById(idUserFirst));
        Optional<User> userSecond = Optional.ofNullable(userDbStorage.getUserById(idUserSecond));

        boolean checkUserIsNotSelf = idUserSecond == idUserFirst;
        if (checkUserIsNotSelf) {
            log.error("Нельзя добавить себя в друзья или удалить самого себя: {}", userFirst);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя добавить себя в друзья или удалить самого себя: " + userFirst);
        }

        if (userFirst.isEmpty()) {
            log.error("Не найден пользователь для которого нужно добавить друга или удалить по ID: {}", idUserFirst);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для которого нужно добавить или удалить друга по ID: " + idUserFirst);
        }

        if (userSecond.isEmpty()) {
            log.error("Не найден пользователь для добавления или удаления в друзья по ID: {}", idUserSecond);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для добавления или удаления в друзья по ID: " + idUserSecond);
        }
    }
}