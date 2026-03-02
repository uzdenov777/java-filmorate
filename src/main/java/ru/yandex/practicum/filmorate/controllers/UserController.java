package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.dto.UserDto;
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
    public UserDto add(@RequestBody @Valid UserDto userDto) {

        log.info("Adding user: {}", userDto);

        UserDto saved = userService.add(userDto);
        return saved;
    }

    @PutMapping
    public UserDto update(@RequestBody @Valid UserDto userDto) {

        log.info("Updating user with: {}", userDto);

        UserDto saved = userService.update(userDto);
        return saved;
    }

    @GetMapping
    public List<UserDto> getAllUsers(Pageable pageable) {
        log.info("Getting all users");
        return userService.getAllUsers(pageable);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Adding friend");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Removing friend");
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable long id, Pageable pageable) {
        log.info("Getting friends");
        return userService.getAllFriendsByUserId(id, pageable);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getMutualFriends(@PathVariable long id,
                                          @PathVariable long otherId,
                                          Pageable pageable) {
        log.info("Getting friends common");
        return userService.getMutualFriends(id, otherId, pageable);
    }
}