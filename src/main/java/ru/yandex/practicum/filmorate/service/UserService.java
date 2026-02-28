package ru.yandex.practicum.filmorate.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.UserDto;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Log4j2
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendsServer friendsServer;

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, FriendsServer friendsServer, UserMapper userMapper) {

        this.userRepository = userRepository;
        this.friendsServer = friendsServer;
        this.userMapper = userMapper;
    }

    public UserDto add(UserDto newUserDto) {

        setDisplayName(newUserDto);

        User newUser = userMapper.toEntity(newUserDto);
        User saved = userRepository.save(newUser);

        UserDto savedDto = userMapper.toDto(saved);
        return savedDto;
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

        UserDto responseDto = userMapper.toDto(saved);
        return responseDto;
    }

    public List<UserDto> getAllUsers() {

        List<User> allUsers = userRepository.findAll();

        List<UserDto> responseDtos = userMapper.toDtos(allUsers);
        return responseDtos;
    }

    public void addFriend(long idFirstUser, long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndIsNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение

        User firstUserProxy = userRepository.getReferenceById(idFirstUser);
        User secondUserProxy = userRepository.getReferenceById(idSecondUser);

        friendsServer.addFriend(firstUserProxy, secondUserProxy);
    }

    public void removeFriend(long idFirstUser, long idSecondUser) throws ResponseStatusException {
        checkUsersExistAndIsNotEqual(idFirstUser, idSecondUser); // если все хорошо просто не выбросит исключение

        friendsServer.removeFriend(idFirstUser, idSecondUser);
    }

    public List<UserDto> getAllFriendsByUserId(long userId) throws ResponseStatusException {

        boolean existsUser = userRepository.existsById(userId);
        if (!existsUser) {
            log.info("Не найден пользователь с ID: {}, для возращения списка его друзей", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь с ID: " + userId + ", для возращения списка его друзей");
        }

        List<User> friendsByUser = friendsServer.getAllFriendsByUserId(userId);

        List<UserDto> responseDtos = userMapper.toDtos(friendsByUser);
        return responseDtos;
    }

    public List<UserDto> getMutualFriends(long idUserFirst, long idUserSecond) throws ResponseStatusException {
        checkUsersExistAndIsNotEqual(idUserFirst, idUserSecond);

        List<User> mutualFriends = friendsServer.getMutualFriends(idUserFirst, idUserSecond);

        List<UserDto> responseDtos = userMapper.toDtos(mutualFriends);
        return responseDtos;
    }

    public boolean isUserExists(Long userId) {

        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            log.info("Не найден пользователь с ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь с ID: " + userId);
        }

        return true;
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

        String setNameUser = (userName == null || userName.isBlank()) ? loginUser : userName;
        user.setName(setNameUser);
    }

    public User getUserProxyById(long userId) {

        User userProxy = userRepository.getReferenceById(userId);
        return userProxy;
    }
}