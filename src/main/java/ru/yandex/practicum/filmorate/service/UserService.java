package ru.yandex.practicum.filmorate.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Log4j2
@Service
public class UserService {
    private final UserDbStorage userDbStorage;
    private final FriendsServer friendsServer;

    @Autowired
    public UserService(UserDbStorage userDbStorage, FriendsServer friendsServer) {
        this.userDbStorage = userDbStorage;
        this.friendsServer = friendsServer;
    }

    public User add(User user) {
        setDisplayName(user);
        userDbStorage.add(user);

        Set<Long> friends = user.getFriends();
        if (!friends.isEmpty()) {
            Long userId = user.getId();
            addFriends(userId, friends);
        }

        return user;
    }

    public User update(User user) {
        Long userId = user.getId();
        boolean userExists = userDbStorage.isUserExists(userId);
        if (!userExists) {
            log.info("Не найден пользователь для обновления с ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для обновления с ID: " + userId);
        }

        setDisplayName(user);
        userDbStorage.update(user);

        Set<Long> friends = user.getFriends();
        if (!friends.isEmpty()) {
            removeFriends(userId, friends); //удаляем потому что старые записи если есть, могут быть не актуальными
            addFriends(userId, friends); //добавляем по той же причине
        }

        return user;
    }

    public List<User> getAllUsers() {
        List<User> allUsers = userDbStorage.getAllUsers();

        for (User user : allUsers) {
            Set<Long> friends = user.getFriends();
            Long userId = user.getId();
            friends.addAll(
                    friendsServer.getAllFriendsIdByUserId(userId));
        }

        return allUsers;
    }

    public void addFriend(long idFirstUser, long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение
        friendsServer.addFriend(idFirstUser, idSecondUser);
    }

    public void addFriends(Long userId, Set<Long> friendsId) throws ResponseStatusException {
        for (Long friendId : friendsId) {
            addFriend(userId, friendId);
        }
    }

    public void removeFriend(long idFirstUser, long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение
        friendsServer.removeFriend(idFirstUser, idSecondUser);
    }

    public void removeFriends(Long userId, Set<Long> friendsId) throws ResponseStatusException {
        for (Long friendId : friendsId) {
            removeFriend(userId, friendId);
        }
    }

    public User getUserById(long userId) throws ResponseStatusException {
        boolean existsUser = userDbStorage.isUserExists(userId);

        if (!existsUser) {
            log.info("Не найден пользователь для возвращения с ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь с ID: " + userId);
        }

        User user = userDbStorage.getUserById(userId);
        if (Objects.nonNull(user)) {
            Set<Long> friends = user.getFriends();
            friends.addAll(
                    friendsServer.getAllFriendsIdByUserId(userId)
            );
        }

        return user;
    }

    public List<User> getAllFriendsByUserId(long userId) throws ResponseStatusException {
        boolean existsUser = userDbStorage.isUserExists(userId);

        if (!existsUser) {
            log.info("Не найден пользователь с ID: {}, для возращения списка его друзей", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь с ID: " + userId + ", для возращения списка его друзей");
        }

        List<Long> allFriendsIdByUserId = friendsServer.getAllFriendsIdByUserId(userId);

        List<User> allFriends = new ArrayList<>();
        for (Long friendId : allFriendsIdByUserId) {
            allFriends.add(userDbStorage.getUserById(friendId));
        }

        return allFriends;
    }

    public List<User> getListMutualFriends(long idUserFirst, long idUserSecond) throws ResponseStatusException {
        checkUsersExistAndNotEqual(idUserFirst, idUserSecond);

        List<Long> friendsUserFirst = friendsServer.getAllFriendsIdByUserId(idUserFirst);
        List<Long> friendsUserSecond = friendsServer.getAllFriendsIdByUserId(idUserSecond);
        friendsUserFirst.retainAll(friendsUserSecond);

        List<User> mutualFriends = new ArrayList<>();
        for (long id : friendsUserFirst) {
            mutualFriends.add(userDbStorage.getUserById(id));
        }
        return mutualFriends;
    }

    public boolean isUserExists(Long userId) {
        boolean userExists = userDbStorage.isUserExists(userId);
        if (!userExists) {
            log.info("Не найден пользователь с ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь с ID: " + userId);
        }

        return true;
    }

    // Проверяет, существуют ли пользователи и не доб. или удал. самого себя
    private void checkUsersExistAndNotEqual(long idUserFirst, long idUserSecond) throws ResponseStatusException {
        boolean checkUserIsNotSelf = idUserSecond == idUserFirst;
        if (checkUserIsNotSelf) {
            log.error("Нельзя добавить себя в друзья или удалить самого себя: {}", idUserFirst);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя добавить себя в друзья или удалить самого себя: " + idUserFirst);
        }

        boolean userFirstExists = userDbStorage.isUserExists(idUserFirst);
        boolean userSecondExists = userDbStorage.isUserExists(idUserSecond);

        if (!userFirstExists) {
            log.error("Не найден пользователь для которого нужно добавить друга или удалить по ID: {}", idUserFirst);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для которого нужно добавить или удалить друга по ID: " + idUserFirst);
        }

        if (!userSecondExists) {
            log.error("Не найден пользователь для добавления или удаления в друзья по ID: {}", idUserSecond);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для добавления или удаления в друзья по ID: " + idUserSecond);
        }
    }

    private void setDisplayName(User user) {
        String userName = user.getName();
        String loginUser = user.getLogin();

        String setNameUser = (userName == null || userName.isBlank()) ? loginUser : userName;
        user.setName(setNameUser);
    }
}