package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    HashMap<Integer, User> users = new HashMap<>();
    int newIdUser = 1;

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Adding user:{}", user);
        user.setDisplayName(user.getName(), user.getLogin());
        user.setId(newIdUser);
        users.put(user.getId(), user);
        newIdUser++;
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        int userId = user.getId();
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

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Getting all users");
        return new ArrayList<>(users.values());
    }

//    private void etttt(User user) {
//        user.setId(id);
//        users.put(user.getId(), user);
//        id++;
//    }
}