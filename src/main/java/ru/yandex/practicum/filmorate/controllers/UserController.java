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
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Adding user");
        return userService.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Updating user with");
        return userService.update(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public String addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Adding friend");
        userService.addFriend(id, friendId);
        return "Пользователи с ID: " + id + " и ID: " + friendId + " теперь друзья";
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public String removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Removing friend");
        userService.deleteFriend(id, friendId);
        return "Пользователи с ID: " + id + " и ID: " + friendId + " теперь не друзья";
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Getting friends");
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getFriendsCommon(@PathVariable int id, @PathVariable int otherId) {
        log.info("Getting friends common");
        return userService.getListMutualFriends(id, otherId);
    }
}
//    PUT /users/{id}/friends/{friendId} — добавление в друзья.
//    DELETE /users/{id}/friends/{friendId} — удаление из друзей.
//    GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
//    GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.