package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UsersStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
@Component
public class InMemoryUserStorage implements UsersStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private static long newIdFilm;

    @Override
    public User add(User user) {
        log.info("Adding user:{}", user);

        setDisplayName(user);
        user.setId(getNewId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User user) throws ResponseStatusException {
        long userId = user.getId();

        if (users.containsKey(userId)) {
            log.info("Updating user with ID:{}", userId);
            setDisplayName(user);
            users.put(userId, user);
            return user;
        } else {
            log.error("No user with ID:{}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with ID:" + userId);
        }
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Getting all users");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    private long getNewId() { //Генерирует уникальный ID.
        newIdFilm++;
        return newIdFilm;
    }

    private void setDisplayName(User user) {
        String userName = user.getName();
        String loginUser = user.getLogin();

        String setNameUser = (userName == null || userName.isBlank()) ? loginUser : userName;
        user.setName(setNameUser);
    }
}