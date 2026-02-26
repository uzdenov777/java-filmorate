package ru.yandex.practicum.filmorate.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.UserDto;
import ru.yandex.practicum.filmorate.storage.interfaces.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendsServer friendsServer;

    @Autowired
    public UserService(UserRepository userRepository, FriendsServer friendsServer) {

        this.userRepository = userRepository;
        this.friendsServer = friendsServer;
    }

    public UserDto add(UserDto newUserDto) {

        User newUser = toUser(newUserDto);

        setDisplayName(newUser);

        User saved = userRepository.save(newUser);

        UserDto savedDto = toUserDto(saved);

        return savedDto;
    }

    public UserDto update(UserDto userDto) {

        Long userId = userDto.getId();

        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            log.info("Не найден пользователь для обновления с ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь для обновления с ID: " + userId);
        }

        User updatedUser = toUser(userDto);

        setDisplayName(updatedUser);

        User saved = userRepository.save(updatedUser);

        UserDto responseDto = toUserDto(saved);

        return responseDto;
    }

    public List<UserDto> getAllUsers() {

        List<User> allUsers = userRepository.findAll();

        List<UserDto> userDtos = new ArrayList<>();
        for (User user : allUsers) {

            UserDto userDto = toUserDto(user);

            userDtos.add(userDto);
        }

        return userDtos;
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

    public List<User> getAllFriendsByUserId(long userId) throws ResponseStatusException {

        boolean existsUser = userRepository.existsById(userId);
        if (!existsUser) {
            log.info("Не найден пользователь с ID: {}, для возращения списка его друзей", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь с ID: " + userId + ", для возращения списка его друзей");
        }

        List<User> friendsByUser = friendsServer.getAllFriendsByUserId(userId);

        return friendsByUser;
    }

    public List<User> getMutualFriends(long idUserFirst, long idUserSecond) throws ResponseStatusException {
        checkUsersExistAndIsNotEqual(idUserFirst, idUserSecond);

        List<User> mutualFriends = friendsServer.getMutualFriends(idUserFirst, idUserSecond);

        return mutualFriends;
    }

    public boolean isUserExists(Long userId) {

        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            log.info("Не найден пользователь с ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найден пользователь с ID: " + userId);
        }

        return true;
    }

    private UserDto toUserDto(User saved) {

        Long id = saved.getId();
        String name = saved.getName();
//        Set<Long> friends = saved.getFriends();
        String email = saved.getEmail();
        String login = saved.getLogin();
        LocalDate birthday = saved.getBirthday();

        UserDto user = new UserDto();
        user.setId(id);
        user.setName(name);
//        user.setFriends(friends);
        user.setEmail(email);
        user.setLogin(login);
        user.setBirthday(birthday);

        return user;
    }

    private User toUser(UserDto newUserRequest) {

        Long id = newUserRequest.getId();
        String name = newUserRequest.getName();
        Set<Long> friends = newUserRequest.getFriends();
        String email = newUserRequest.getEmail();
        String login = newUserRequest.getLogin();
        LocalDate birthday = newUserRequest.getBirthday();

        User user = new User();
        user.setId(id);
        user.setName(name);
//        user.setFriends(friends);
        user.setEmail(email);
        user.setLogin(login);
        user.setBirthday(birthday);

        return user;
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

    private void setDisplayName(User user) {

        String userName = user.getName();
        String loginUser = user.getLogin();

        String setNameUser = (userName == null || userName.isBlank()) ? loginUser : userName;
        user.setName(setNameUser);
    }
}