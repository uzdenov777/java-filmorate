package ru.yandex.practicum.filmorate.storage;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private long newIdFilm;

    @Override
    public User add(User user) {
        log.info("Adding user:{}", user);

        user.setDisplayName(user.getName(), user.getLogin());
        user.setId(getNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) throws ResponseStatusException {
        long userId = user.getId();

        if (users.containsKey(userId)) {
            log.info("Updating user with ID:{}", userId);
            user.setDisplayName(user.getName(), user.getLogin());
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

    private long getNewId() { //Генерирует уникальный ID.
        newIdFilm++;
        return newIdFilm;
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }
}