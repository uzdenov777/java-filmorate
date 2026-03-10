package ru.yandex.practicum.filmorate.service;

import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.EventDto;
import ru.yandex.practicum.filmorate.model.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.dto.UserDto;
import ru.yandex.practicum.filmorate.repository.FilmsRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.yandex.practicum.filmorate.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.enums.Operation.REMOVE;

@Log4j2
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendsServer friendsServer;
    private final FilmsRepository filmsRepository;

    private final UserMapper userMapper;
    private final FilmMapper filmMapper;
    private final EventService eventService;

    @Autowired
    public UserService(UserRepository userRepository, FriendsServer friendsServer, FilmsRepository filmsRepository, UserMapper userMapper, FilmMapper filmMapper, EventService eventService) {

        this.userRepository = userRepository;
        this.friendsServer = friendsServer;
        this.filmsRepository = filmsRepository;
        this.userMapper = userMapper;
        this.filmMapper = filmMapper;
        this.eventService = eventService;
    }

    public UserDto add(UserDto newUserDto) {

        setDisplayName(newUserDto);

        User newUser = userMapper.toEntity(newUserDto);
        User saved = userRepository.save(newUser);

        return userMapper.toDto(saved);
    }

    public UserDto update(UserDto userDto) {

        setDisplayName(userDto);

        Long userId = userDto.getId();
        boolean userExists = userRepository.existsById(userId);

        if (!userExists) {
            log.info("Не найден пользователь для обновления с ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для обновления с ID: " + userId);
        }

        User updatedUser = userMapper.toEntity(userDto);
        User saved = userRepository.save(updatedUser);

        return userMapper.toDto(saved);
    }

    public void deleteUser(Long userId) {
        boolean isExists = userRepository.existsById(userId);

        if (!isExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND
                    , "Не найден пользователь для удаления: " + userId);
        }

        userRepository.deleteById(userId);
    }

    public UserDto getDtoById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public List<UserDto> getAllUsers(Pageable pageable) {

        Page<User> allUsers = userRepository.findAll(pageable);

        return userMapper.toDtos(allUsers);
    }

    public User getUserProxyById(long userId) {
        return userRepository.getReferenceById(userId);
    }


    public void addFriend(long idFirstUser, long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndIsNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение

        User firstUserProxy = userRepository.getReferenceById(idFirstUser);
        User secondUserProxy = userRepository.getReferenceById(idSecondUser);

        friendsServer.addFriend(firstUserProxy, secondUserProxy);
        eventService.save(firstUserProxy, idSecondUser, FRIEND, ADD);
    }

    public void removeFriend(long idFirstUser, long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndIsNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение

        User firstUserProxy = userRepository.getReferenceById(idFirstUser);

        friendsServer.removeFriend(idFirstUser, idSecondUser);
        eventService.save(firstUserProxy, idSecondUser, FRIEND, REMOVE);
    }

    public List<UserDto> getAllFriendsByUserId(long userId, Pageable pageable) throws ResponseStatusException {

        boolean existsUser = userRepository.existsById(userId);
        if (!existsUser) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь с ID: " + userId + ", для возращения списка его друзей");
        }

        Page<User> friendsByUser = friendsServer.getAllFriendsByUserId(userId, pageable);

        return userMapper.toDtos(friendsByUser);
    }

    public List<UserDto> getMutualFriends(long idUserFirst, long idUserSecond, Pageable pageable) throws ResponseStatusException {
        checkUsersExistAndIsNotEqual(idUserFirst, idUserSecond);

        Page<User> mutualFriends = friendsServer.getMutualFriends(idUserFirst, idUserSecond, pageable);

        return userMapper.toDtos(mutualFriends);
    }

    public boolean isUserExists(Long userId) {

        return userRepository.existsById(userId);
    }

    public List<FilmDto> getRecommendations(long id) {
        var isExistUser = userRepository.existsById(id);
        if (!isExistUser) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден пользователь: " + id + " для возвращения рекомендаций");
        }

        var similarUserId = userRepository.findSimilarUserByUserId(id);
        if (similarUserId == null) {
            return List.of();
        }

        List<Film> recommendations = filmsRepository.findRecommendations(id, similarUserId);

        return filmMapper.toDtos(recommendations);
    }

    public Set<EventDto> getEvents(Long userId) {
        var isExistUser = userRepository.existsById(userId);
        if (!isExistUser) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Не найден пользователь: " + userId + " для возвращения его ленты событий");
        }

        Set<Long> ids = friendsServer.findFriendIdsByUserId(userId);

        return eventService.findByUserIds(ids);
    }

    // Проверяет, существуют ли пользователи и не доб. или удал. самого себя
    private void checkUsersExistAndIsNotEqual(long idUserFirst, long idUserSecond) throws ResponseStatusException {

        boolean checkUserIsNotSelf = idUserSecond == idUserFirst;
        if (checkUserIsNotSelf) {
            log.error("Нельзя добавить себя в друзья или удалить самого себя: {}", idUserFirst);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя добавить себя в друзья или удалить самого себя: " + idUserFirst);
        }

        boolean userFirstExists = userRepository.existsById(idUserFirst);
        boolean userSecondExists = userRepository.existsById(idUserSecond);

        if (!userFirstExists) {
            log.error("Не найден пользователь для которого нужно добавить друга или удалить по ID: {}", idUserFirst);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для которого нужно добавить или удалить друга по ID: " + idUserFirst);
        }

        if (!userSecondExists) {
            log.error("Не найден пользователь для добавления или удаления в друзья по ID: {}", idUserSecond);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для добавления или удаления в друзья по ID: " + idUserSecond);
        }
    }

    private void setDisplayName(UserDto user) {
        String userName = user.getName();
        String loginUser = user.getLogin();

        String setNameUser = (StringUtils.isBlank(userName)) ? loginUser : userName;
        user.setName(setNameUser);
    }
}