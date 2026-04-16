package ru.yandex.practicum.filmorate.user;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.event.EventService;
import ru.yandex.practicum.filmorate.event.model.dto.EventDto;
import ru.yandex.practicum.filmorate.film.FilmMapper;
import ru.yandex.practicum.filmorate.film.FilmsRepository;
import ru.yandex.practicum.filmorate.film.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.friendship.FriendsServer;
import ru.yandex.practicum.filmorate.user.model.User;
import ru.yandex.practicum.filmorate.user.model.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.yandex.practicum.filmorate.event.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.event.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.event.enums.Operation.REMOVE;

@Log4j2
@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FilmsRepository filmsRepository;

    private final FriendsServer friendsServer;
    private final EventService eventService;

    private final UserMapper userMapper;
    private final FilmMapper filmMapper;

    public UserDto add(UserDto dto) {
        setDisplayName(dto);

        var newUser = userMapper.toEntity(dto);

        var saved = userRepository.save(newUser);
        return userMapper.toDto(saved);
    }

    public UserDto update(UserDto dto) {
        Long id = dto.getId();

        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "При обновлении выявлено, что ID из тела запроса NULL");
        }

        var userExists = userRepository.existsById(id);
        if (!userExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден пользователь для обновления с ID: " + id);
        }

        setDisplayName(dto);

        var updatedUser = userMapper.toEntity(dto);

        var saved = userRepository.save(updatedUser);
        return userMapper.toDto(saved);
    }

    public void deleteUser(Long userId) {
        var isExists = isUserExists(userId);
        if (!isExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND
                    , "Не найден пользователь для удаления: " + userId);
        }

        userRepository.deleteById(userId);
    }

    public UserDto getDtoById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Для возвращения не найден пользователь по ID: " + id));
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public List<UserDto> getAllUsers(Pageable pageable) {
        var allUsers = userRepository.findAll(pageable);
        return userMapper.toDtos(allUsers);
    }

    public User getUserProxyById(long userId) {
        return userRepository.getReferenceById(userId);
    }

    public void addFriend(Long idFirstUser, Long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndIsNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение

        var firstUserProxy = userRepository.getReferenceById(idFirstUser);
        var secondUserProxy = userRepository.getReferenceById(idSecondUser);

        friendsServer.addFriend(firstUserProxy, secondUserProxy);
        eventService.save(firstUserProxy, idSecondUser, FRIEND, ADD);
    }

    public void removeFriend(Long idFirstUser, Long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndIsNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение

        friendsServer.removeFriend(idFirstUser, idSecondUser);

        var firstUserProxy = userRepository.getReferenceById(idFirstUser);
        eventService.save(firstUserProxy, idSecondUser, FRIEND, REMOVE);
    }

    public List<UserDto> getAllFriendsByUserId(Long userId, Pageable pageable) throws ResponseStatusException {
        var existsUser = isUserExists(userId);
        if (!existsUser) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден пользователь с ID: " + userId + ", для возращения списка его друзей");
        }

        var friendsByUser = friendsServer.getAllFriendsByUserId(userId, pageable);
        return userMapper.toDtos(friendsByUser);
    }

    public List<UserDto> getMutualFriends(Long idUserFirst, Long idUserSecond, Pageable pageable) throws ResponseStatusException {
        checkUsersExistAndIsNotEqual(idUserFirst, idUserSecond);

        var mutualFriends = friendsServer.getMutualFriends(idUserFirst, idUserSecond, pageable);
        return userMapper.toDtos(mutualFriends);
    }

    public boolean isUserExists(Long userId) {
        return userRepository.existsById(userId);
    }

    public List<FilmDto> getRecommendations(Long id) {
        var isExistUser = isUserExists(id);
        if (!isExistUser) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден пользователь: " + id + " для возвращения рекомендаций");
        }

        var similarUserId = userRepository.findSimilarUserByUserId(id);
        if (similarUserId == null) {
            return List.of();
        }

        var recommendations = filmsRepository.findRecommendations(id, similarUserId);
        return filmMapper.toDtos(recommendations);
    }

    public Set<EventDto> getEvents(Long userId) {
        var isExistUser = isUserExists(userId);
        if (!isExistUser) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден пользователь: " + userId + " для возвращения его ленты событий");
        }

        return eventService.findByUserId(userId);
    }

    // Проверяет, существуют ли пользователи и не доб. или удал. самого себя
    private void checkUsersExistAndIsNotEqual(Long idUserFirst, Long idUserSecond) throws ResponseStatusException {
        var checkUserIsNotSelf = idUserSecond.equals(idUserFirst);
        if (checkUserIsNotSelf) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Нельзя добавить себя в друзья или удалить самого себя: " + idUserFirst);
        }

        var userFirstExists = isUserExists(idUserFirst);
        var userSecondExists = isUserExists(idUserSecond);

        if (!userFirstExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден пользователь для которого нужно добавить или удалить друга по ID: " + idUserFirst);
        }

        if (!userSecondExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден пользователь для добавления или удаления в друзья по ID: " + idUserSecond);
        }
    }

    private void setDisplayName(UserDto user) {
        var userName = user.getName();
        var loginUser = user.getLogin();

        var setNameUser = (StringUtils.isBlank(userName)) ? loginUser : userName;
        user.setName(setNameUser);
    }
}