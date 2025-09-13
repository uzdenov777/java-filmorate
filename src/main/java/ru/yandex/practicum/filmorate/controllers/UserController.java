package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Adding user: {}", user);
        User save = userService.add(user);
        log.info("Saving user: {}", save);
        return save;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Updating user with: {}", user);
        User save = userService.update(user);
        log.info("Saving user with: {}", save);
        return save;
    }

    @GetMapping("/{id}")
    public User getByIdUser(@PathVariable long id) {
        log.info("Get user by id: {}", id);
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void

    addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Adding friend");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Removing friend");
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Getting friends");
        return userService.getAllFriendsByUserId(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getFriendsCommon(@PathVariable int id, @PathVariable int otherId) {
        log.info("Getting friends common");
        return userService.getListMutualFriends(id, otherId);
    }
}