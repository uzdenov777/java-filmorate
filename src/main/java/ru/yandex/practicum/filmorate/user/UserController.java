package ru.yandex.practicum.filmorate.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.event.model.dto.EventDto;
import ru.yandex.practicum.filmorate.film.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.user.model.dto.UserDto;

import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto add(@RequestBody @Valid UserDto userDto) {
        log.info("Adding user: {}", userDto);

        return userService.add(userDto);
    }

    @PutMapping
    public UserDto update(@RequestBody @Valid UserDto userDto) {
        log.info("Updating user with: {}", userDto);

        return userService.update(userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Удаление пользователя: {}", userId);

        userService.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Вернуть пользователя: {}", id);

        return userService.getDtoById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers(Pageable pageable) {
        log.info("Getting all users");

        return userService.getAllUsers(pageable);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Adding friend");

        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Removing friend");

        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable Long id, Pageable pageable) {
        log.info("Getting friends");

        return userService.getAllFriendsByUserId(id, pageable);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getMutualFriends(@PathVariable Long id,
                                          @PathVariable Long otherId,
                                          Pageable pageable) {
        log.info("Getting friends common");

        return userService.getMutualFriends(id, otherId, pageable);
    }

    @GetMapping("/{id}/recommendations")
    public List<FilmDto> getRecommendations(@PathVariable Long id) {
        log.info("Вернуть рекомендации для пользователя: {}", id);

        return userService.getRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public Set<EventDto> getEvents(@PathVariable Long id) {
        log.info("Вернуть список событий пользователя: {}", id);

        return userService.getEvents(id);
    }
}