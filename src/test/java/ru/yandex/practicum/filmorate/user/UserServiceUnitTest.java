package ru.yandex.practicum.filmorate.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.event.EventService;
import ru.yandex.practicum.filmorate.event.enums.EventType;
import ru.yandex.practicum.filmorate.event.enums.Operation;
import ru.yandex.practicum.filmorate.film.FilmMapper;
import ru.yandex.practicum.filmorate.film.FilmMapperImpl;
import ru.yandex.practicum.filmorate.film.FilmsRepository;
import ru.yandex.practicum.filmorate.friendship.FriendsServer;
import ru.yandex.practicum.filmorate.user.model.User;
import ru.yandex.practicum.filmorate.user.model.dto.UserDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.yandex.practicum.filmorate.event.enums.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.event.enums.Operation.ADD;
import static ru.yandex.practicum.filmorate.event.enums.Operation.REMOVE;

@ExtendWith(SpringExtension.class)
class UserServiceUnitTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private FilmsRepository filmsRepository;
    @Mock
    private FriendsServer friendsServer;
    @Mock
    private EventService eventService;

    private UserMapper userMapper;
    private FilmMapper filmMapper;

    private UserDto firstDto;
    private UserDto mutualFriend;

    private User firstUserProxy;
    private User secondUserProxy;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
        filmMapper = new FilmMapperImpl();
        userService = new UserService(userRepository, filmsRepository, friendsServer, eventService, userMapper, filmMapper);

        firstDto = new UserDto();
        firstDto.setName("firstDto");
        firstDto.setEmail("firstDto@mail.ru");
        firstDto.setLogin("firstDto");
        firstDto.setBirthday(LocalDate.of(1980, 1, 1));

        mutualFriend = new UserDto();
        mutualFriend.setName("mutualFriend");
        mutualFriend.setLogin("mutualFriend");
        mutualFriend.setEmail("mutualFriend@gmail.com");
        mutualFriend.setBirthday(LocalDate.of(1980, 1, 1));

        firstUserProxy = new User();
        secondUserProxy = new User();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void add_whenRequestIsValid_thenSavedAndReturnUserDto() {
        //given
        when(userRepository.save(any(User.class)))
                .thenAnswer(i ->
                {
                    User user = i.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        //when
        UserDto resDto = userService.add(firstDto);

        //then
        assertEquals(1L, resDto.getId());
        assertEquals(firstDto.getName(), resDto.getName());
        assertEquals(firstDto.getLogin(), resDto.getLogin());
        assertEquals(firstDto.getEmail(), resDto.getEmail());
        assertEquals(firstDto.getBirthday(), resDto.getBirthday());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void add_whenNameEmpty_thenSubstituteValueFromLoginIntoNameAndSavedAndReturnUserDto() {
        //given
        firstDto.setName("");

        when(userRepository.save(any(User.class)))
                .thenAnswer(i ->
                {
                    User user = i.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        //when
        UserDto resDto = userService.add(firstDto);

        //then
        assertEquals(1L, resDto.getId());
        assertEquals(firstDto.getName(), resDto.getLogin()); //логин вместо имени если оно пришло пустое
        assertEquals(firstDto.getLogin(), resDto.getLogin());
        assertEquals(firstDto.getEmail(), resDto.getEmail());
        assertEquals(firstDto.getBirthday(), resDto.getBirthday());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_whenRequestIsValid_thenSavedAndReturnUserDto() {
        //given
        Long id = 1L;
        firstDto.setId(id);

        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        //when
        UserDto resDto = userService.update(firstDto);

        //then
        assertEquals(1L, resDto.getId());
        assertEquals(firstDto.getName(), resDto.getName());
        assertEquals(firstDto.getLogin(), resDto.getLogin());
        assertEquals(firstDto.getEmail(), resDto.getEmail());
        assertEquals(firstDto.getBirthday(), resDto.getBirthday());

        verify(userRepository).existsById(id);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_whenNameEmpty_thenSubstituteValueFromLoginIntoNameAndSavedAndReturnUserDto() {
        //given
        Long id = 1L;

        firstDto.setId(id);
        firstDto.setName("");//установили пустое имя

        when(userRepository.existsById(id)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        //when
        UserDto resDto = userService.update(firstDto);

        //then
        assertEquals(1L, resDto.getId());
        assertEquals(firstDto.getName(), resDto.getLogin()); //логин вместо имени если оно пришло пустое
        assertEquals(firstDto.getLogin(), resDto.getLogin());
        assertEquals(firstDto.getEmail(), resDto.getEmail());
        assertEquals(firstDto.getBirthday(), resDto.getBirthday());

        verify(userRepository).existsById(id);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_whenIdNull_thenThrowResponseStatusExceptionBadRequest() {
        //given
        firstDto.setId(null); //id в userDto из тела запроса пустой

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> userService.update(firstDto));

        //then
        HttpStatus statusExpected = HttpStatus.BAD_REQUEST;
        String messageExpected = "400 BAD_REQUEST \"При обновлении выявлено, что ID из тела запроса NULL\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository, never()).existsById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_whenUserNotExist_thenThrowResponseStatusExceptionNotFound() {
        //given
        Long notExistId = 1L;
        firstDto.setId(notExistId);

        when(userRepository.existsById(notExistId)).thenReturn(false);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> userService.update(firstDto));

        //then
        HttpStatus statusExpected = HttpStatus.NOT_FOUND;
        String messageExpected = "404 NOT_FOUND \"Не найден пользователь для обновления с ID: " + notExistId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository).existsById(notExistId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_whenUserExist_thenRemoveUser() {
        //given
        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(true);

        //when
        userService.deleteUser(id);

        //then
        verify(userRepository).deleteById(id);
    }

    @Test
    void delete_whenUserNotExist_thenThrowResponseStatusExceptionNotFound() {
        //given
        Long notExistId = 1L;

        when(userRepository.existsById(notExistId)).thenReturn(false);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> userService.deleteUser(notExistId));

        //then
        HttpStatus statusExpected = HttpStatus.NOT_FOUND;
        String messageExpected = "404 NOT_FOUND \"Не найден пользователь для удаления: " + notExistId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository).existsById(notExistId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getDtoById_whenUserExist_thenReturnUser() {
        //given
        Long id = 1L;

        when(userRepository.findById(id))
                .thenAnswer(i -> {
                    Long Id = i.getArgument(0);
                    firstDto.setId(Id);

                    User user = userMapper.toEntity(firstDto);
                    return Optional.of(user);
                });

        //when
        UserDto resDto = userService.getDtoById(id);

        //then
        assertEquals(1L, resDto.getId());
        assertEquals(firstDto.getName(), resDto.getName());
        assertEquals(firstDto.getLogin(), resDto.getLogin());
        assertEquals(firstDto.getEmail(), resDto.getEmail());
        assertEquals(firstDto.getBirthday(), resDto.getBirthday());

        verify(userRepository).findById(id);
    }

    @Test
    void getDtoById_whenUserNotExist_thenThrowResponseStatusExceptionNotFound() {
        //given
        Long notExistId = 1L;

        when(userRepository.findById(notExistId)).thenReturn(Optional.empty());

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> userService.getDtoById(notExistId));

        //then
        HttpStatus statusExpected = HttpStatus.NOT_FOUND;
        String messageExpected = "404 NOT_FOUND \"Для возвращения не найден пользователь по ID: " + notExistId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository).findById(notExistId);
    }

    @Test
    void getById_whenUserExist_thenReturnOptionalOfUser() {
        //given
        Long id = 1L;

        when(userRepository.findById(id))
                .thenAnswer(i -> {
                    Long Id = i.getArgument(0);
                    firstDto.setId(Id);

                    User user = userMapper.toEntity(firstDto);
                    return Optional.of(user);
                });

        //when
        Optional<User> resOpt = userService.getById(id);

        //then
        assertTrue(resOpt.isPresent());

        User user = resOpt.get();
        assertEquals(1L, user.getId());
        assertEquals(firstDto.getName(), user.getName());
        assertEquals(firstDto.getLogin(), user.getLogin());
        assertEquals(firstDto.getEmail(), user.getEmail());
        assertEquals(firstDto.getBirthday(), user.getBirthday());

        verify(userRepository).findById(id);
    }

    @Test
    void getById_whenUserNotExist_thenReturnEmptyOptional() {
        //given
        Long notExistId = 1L;

        when(userRepository.findById(notExistId)).thenReturn(Optional.empty());

        //when
        Optional<User> resOpt = userService.getById(notExistId);

        //then
        assertTrue(resOpt.isEmpty());
        verify(userRepository).findById(notExistId);
    }

    @Test
    void getAllUsers_whenUsersExist_thenReturnListOfUsers() {
        //given
        firstDto.setId(1L);
        User user = userMapper.toEntity(firstDto);

        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(page);

        //when
        List<UserDto> resDtos = userService.getAllUsers(pageable); //Pageable вставили чтобы не ругался

        //then
        assertTrue(resDtos.size() > 0);

        UserDto userRes = resDtos.get(0);
        assertEquals(1L, userRes.getId());
        assertEquals(firstDto.getName(), userRes.getName());
        assertEquals(firstDto.getLogin(), userRes.getLogin());
        assertEquals(firstDto.getEmail(), userRes.getEmail());
        assertEquals(firstDto.getBirthday(), userRes.getBirthday());

        verify(userRepository).findAll(pageable);
    }

    @Test
    void getAllUsers_whenUsersNotExists_thenReturnListEmpty() {
        //given
        Page<User> page = new PageImpl<>(List.of());
        when(userRepository.findAll(pageable)).thenReturn(page);

        //when
        List<UserDto> resDtos = userService.getAllUsers(pageable); //Pageable вставили чтобы не ругался

        //then
        assertTrue(resDtos.isEmpty());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void addFriend_whenRequestIsValid_thenAddFriend() {
        //given
        Long firstId = 1L;
        Long secondId = 2L;

        when(userRepository.existsById(firstId)).thenReturn(true);
        when(userRepository.existsById(secondId)).thenReturn(true);

        when(userRepository.getReferenceById(firstId))
                .thenAnswer(i ->
                {
                    Long id = i.getArgument(0);
                    firstUserProxy.setId(id);

                    return firstUserProxy;
                });

        when(userRepository.getReferenceById(secondId))
                .thenAnswer(i ->
                {
                    Long id = i.getArgument(0);
                    secondUserProxy.setId(id);

                    return secondUserProxy;
                });

        //when
        userService.addFriend(firstId, secondId);

        //then
        verify(userRepository).existsById(firstId);
        verify(userRepository).existsById(secondId);
        verify(userRepository).getReferenceById(firstId);
        verify(userRepository).getReferenceById(secondId);
        verify(friendsServer).addFriend(firstUserProxy, secondUserProxy);
        verify(eventService).save(firstUserProxy, secondId, FRIEND, ADD);
    }

    @Test
    void addFriend_whenUserAddsHimselfFriend_thenThrowResponseStatusExceptionBadRequest() {
        //given
        Long firstId = 1L;

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> userService.addFriend(firstId, firstId));

        //then
        HttpStatus statusExpected = HttpStatus.BAD_REQUEST;
        String messageExpected = "400 BAD_REQUEST \"Нельзя добавить себя в друзья или удалить самого себя: " + firstId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository, never()).existsById(anyLong());
        verify(userRepository, never()).getReferenceById(anyLong());
        verify(friendsServer, never()).addFriend(any(User.class), any(User.class));
        verify(eventService, never()).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void addFriend_whenUserAddsNotExistingFriend_thenThrowResponseStatusExceptionNotFound() {
        //given
        Long firstId = 1L;
        Long notExistId = 777L;

        when(userRepository.existsById(firstId)).thenReturn(true);
        when(userRepository.existsById(notExistId)).thenReturn(false);

        //when
        ResponseStatusException resExc =
                assertThrows(ResponseStatusException.class, () -> userService.addFriend(firstId, notExistId));

        //then
        HttpStatus statusExpected = HttpStatus.NOT_FOUND;
        String messageExpected = "404 NOT_FOUND \"Не найден пользователь для добавления или удаления в друзья по ID: " + notExistId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository).existsById(firstId);
        verify(userRepository).existsById(notExistId);

        verify(userRepository, never()).getReferenceById(anyLong());
        verify(friendsServer, never()).addFriend(any(User.class), any(User.class));
        verify(eventService, never()).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void addFriend_whenNotExistingUserAddsFriend_thenThrowResponseStatusExceptionNotFound() {
        //given
        Long notExistId = 777L;
        Long friendId = 2L;

        when(userRepository.existsById(notExistId)).thenReturn(false);
        when(userRepository.existsById(friendId)).thenReturn(true);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> userService.addFriend(notExistId, friendId));

        //then
        HttpStatus statusExpected = HttpStatus.NOT_FOUND;
        String messageExpected = "404 NOT_FOUND \"Не найден пользователь для которого нужно добавить или удалить друга по ID: " + notExistId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository).existsById(notExistId);
        verify(userRepository).existsById(friendId);

        verify(userRepository, never()).getReferenceById(anyLong());
        verify(friendsServer, never()).addFriend(any(User.class), any(User.class));
        verify(eventService, never()).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void removeFriend_whenRequestIsValid_thenRemoveFriend() {
        //given
        Long firstId = 1L;
        Long secondId = 2L;

        when(userRepository.existsById(firstId)).thenReturn(true);
        when(userRepository.existsById(secondId)).thenReturn(true);

        when(userRepository.getReferenceById(firstId))
                .thenAnswer(i ->
                {
                    Long id = i.getArgument(0);
                    firstUserProxy.setId(id);

                    return firstUserProxy;
                });

        //when
        userService.removeFriend(firstId, secondId);

        //then
        verify(userRepository).existsById(firstId);
        verify(userRepository).existsById(secondId);
        verify(userRepository).getReferenceById(firstId);
        verify(friendsServer).removeFriend(firstId, secondId);
        verify(eventService).save(firstUserProxy, secondId, FRIEND, REMOVE);
    }

    @Test
    void removeFriend_whenUserDeletesHimselfFromFriends_thenThrowResponseStatusExceptionBadRequest() {
        //given
        Long firstId = 1L;

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> userService.removeFriend(firstId, firstId));

        //then
        HttpStatus statusExpected = HttpStatus.BAD_REQUEST;
        String messageExpected = "400 BAD_REQUEST \"Нельзя добавить себя в друзья или удалить самого себя: " + firstId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository, never()).existsById(anyLong());
        verify(userRepository, never()).getReferenceById(anyLong());
        verify(friendsServer, never()).removeFriend(anyLong(), anyLong());
        verify(eventService, never()).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void removeFriend_whenUserDeletesNotExistingFriend_thenThrowResponseStatusExceptionNotFound() {
        //given
        Long firstId = 1L;
        Long notExistId = 777L;

        when(userRepository.existsById(firstId)).thenReturn(true);
        when(userRepository.existsById(notExistId)).thenReturn(false);

        //when
        ResponseStatusException resExc =
                assertThrows(ResponseStatusException.class, () -> userService.addFriend(firstId, notExistId));

        //then
        HttpStatus statusExpected = HttpStatus.NOT_FOUND;
        String messageExpected = "404 NOT_FOUND \"Не найден пользователь для добавления или удаления в друзья по ID: " + notExistId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository).existsById(firstId);
        verify(userRepository).existsById(notExistId);

        verify(userRepository, never()).getReferenceById(anyLong());
        verify(friendsServer, never()).removeFriend(anyLong(), anyLong());
        verify(eventService, never()).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void removeFriend_whenNotExistingUserDeletesFriend_thenThrowResponseStatusExceptionNotFound() {
        //given
        Long notExistId = 777L;
        Long friendId = 2L;

        when(userRepository.existsById(notExistId)).thenReturn(false);
        when(userRepository.existsById(friendId)).thenReturn(true);

        //when
        ResponseStatusException resExc = assertThrows(ResponseStatusException.class, () -> userService.addFriend(notExistId, friendId));

        //then
        HttpStatus statusExpected = HttpStatus.NOT_FOUND;
        String messageExpected = "404 NOT_FOUND \"Не найден пользователь для которого нужно добавить или удалить друга по ID: " + notExistId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository).existsById(notExistId);
        verify(userRepository).existsById(notExistId);

        verify(userRepository, never()).getReferenceById(anyLong());
        verify(friendsServer, never()).removeFriend(anyLong(), anyLong());
        verify(eventService, never()).save(any(User.class), anyLong(), any(EventType.class), any(Operation.class));
    }

    @Test
    void getAllFriendsByUserId_whenExistFriends_thenReturnListOfUsers() {
        //given
        Long firstId = 1L;

        firstDto.setId(firstId);

        User user = userMapper.toEntity(firstDto);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.existsById(firstId)).thenReturn(true);
        when(friendsServer.getAllFriendsByUserId(firstId, pageable)).thenReturn(page);

        //when
        List<UserDto> resDtos = userService.getAllFriendsByUserId(firstId, pageable);

        //then
        assertEquals(1, resDtos.size());

        UserDto userRes = resDtos.get(0);
        assertEquals(1L, userRes.getId());
        assertEquals(firstDto.getName(), userRes.getName());
        assertEquals(firstDto.getLogin(), userRes.getLogin());
        assertEquals(firstDto.getEmail(), userRes.getEmail());
        assertEquals(firstDto.getBirthday(), userRes.getBirthday());

        verify(userRepository).existsById(firstId);
        verify(friendsServer).getAllFriendsByUserId(firstId, pageable);
    }

    @Test
    void getAllFriendsByUserId_whenNotExistFriends_thenReturnEmptyList() {
        //given
        Long firstId = 1L;

        Page<User> page = new PageImpl<>(List.of());

        when(userRepository.existsById(firstId)).thenReturn(true);
        when(friendsServer.getAllFriendsByUserId(firstId, pageable)).thenReturn(page);

        //when
        List<UserDto> users = userService.getAllFriendsByUserId(firstId, pageable);

        //then
        assertTrue(users.isEmpty());

        verify(userRepository).existsById(firstId);
        verify(friendsServer).getAllFriendsByUserId(firstId, pageable);
    }

    @Test
    void getAllFriendsByUserId_whenNotExistUser_thenReturn404() {
        //given
        Long nonExistId = 777L;

        when(userRepository.existsById(nonExistId)).thenReturn(false);

        //when
        ResponseStatusException resExc =
                assertThrows(ResponseStatusException.class, () -> userService.getAllFriendsByUserId(nonExistId, pageable));

        //then
        HttpStatus statusExpected = HttpStatus.NOT_FOUND;
        String messageExpected = "404 NOT_FOUND \"Не найден пользователь с ID: " + nonExistId + ", для возращения списка его друзей\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository).existsById(nonExistId);
        verify(friendsServer, never()).getAllFriendsByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    void getMutualFriends_whenExistMutualFriends_thenReturnListOfUsers() {
        //given
        Long firstId = 1L;
        Long secondId = 2L;
        Long mutualId = 3L;

        mutualFriend.setId(mutualId);
        User mutualUser = userMapper.toEntity(mutualFriend);

        Page<User> page = new PageImpl<>(List.of(mutualUser));

        when(userRepository.existsById(firstId)).thenReturn(true);
        when(userRepository.existsById(secondId)).thenReturn(true);
        when(friendsServer.getMutualFriends(firstId, secondId, pageable)).thenReturn(page);

        //when
        List<UserDto> resDtos = userService.getMutualFriends(firstId, secondId, pageable);

        //then
        assertEquals(1, resDtos.size());

        UserDto userRes = resDtos.get(0);
        assertEquals(3L, userRes.getId());
        assertEquals(mutualFriend.getName(), userRes.getName());
        assertEquals(mutualFriend.getLogin(), userRes.getLogin());
        assertEquals(mutualFriend.getEmail(), userRes.getEmail());
        assertEquals(mutualFriend.getBirthday(), userRes.getBirthday());

        verify(userRepository).existsById(firstId);
        verify(userRepository).existsById(secondId);
        verify(friendsServer).getMutualFriends(firstId, secondId, pageable);
    }

    @Test
    void getMutualFriends_whenNotExistMutualFriends_thenReturnEmptyList() {
        //given
        Long firstId = 1L;
        Long secondId = 2L;

        Page<User> page = new PageImpl<>(List.of());

        when(userRepository.existsById(firstId)).thenReturn(true);
        when(userRepository.existsById(secondId)).thenReturn(true);
        when(friendsServer.getMutualFriends(firstId, secondId, pageable)).thenReturn(page);

        //when
        List<UserDto> resDtos = userService.getMutualFriends(firstId, secondId, pageable);

        //then
        assertTrue(resDtos.isEmpty());

        verify(userRepository).existsById(firstId);
        verify(userRepository).existsById(secondId);
        verify(friendsServer).getMutualFriends(firstId, secondId, pageable);
    }

    @Test
    void getMutualFriends_whenUserRequestCommonFriendsHimself_thenThrowResponseStatusExceptionBadRequest() {
        //given
        Long firstId = 1L;

        //when
        ResponseStatusException resExc
                = assertThrows(ResponseStatusException.class, () -> userService.getMutualFriends(firstId, firstId, pageable));

        //then
        HttpStatus statusExpected = HttpStatus.BAD_REQUEST;
        String messageExpected = "400 BAD_REQUEST \"Нельзя добавить себя в друзья или удалить самого себя: " + firstId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository, never()).existsById(anyLong());
        verify(friendsServer, never()).getMutualFriends(anyLong(), anyLong(), any(Pageable.class));
    }

    @Test
    void getMutualFriends_whenFirstUserNotExist_thenThrowResponseStatusExceptionNotFound() {
        //given
        Long notExistId = 777L;
        Long secondId = 2L;

        when(userRepository.existsById(notExistId)).thenReturn(false);
        when(userRepository.existsById(secondId)).thenReturn(true);

        //when
        ResponseStatusException resExc
                = assertThrows(ResponseStatusException.class, () -> userService.getMutualFriends(notExistId, secondId, pageable));

        //then
        HttpStatus statusExpected = HttpStatus.NOT_FOUND;
        String messageExpected = "404 NOT_FOUND \"Не найден пользователь для которого нужно добавить или удалить друга по ID: " + notExistId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository).existsById(notExistId);
        verify(userRepository).existsById(secondId);
        verify(friendsServer, never()).getMutualFriends(anyLong(), anyLong(), any(Pageable.class));
    }

    @Test
    void getMutualFriends_whenSecondUserNotExist_thenThrowResponseStatusExceptionNotFound() {
        //given
        Long firstId = 1L;
        Long notExistId = 777L;

        when(userRepository.existsById(firstId)).thenReturn(true);
        when(userRepository.existsById(notExistId)).thenReturn(false);

        //when
        ResponseStatusException resExc
                = assertThrows(ResponseStatusException.class, () -> userService.getMutualFriends(firstId, notExistId, pageable));

        //then
        HttpStatus statusExpected = HttpStatus.NOT_FOUND;
        String messageExpected = "404 NOT_FOUND \"Не найден пользователь для добавления или удаления в друзья по ID: " + notExistId + "\"";

        assertEquals(statusExpected, resExc.getStatusCode());
        assertEquals(messageExpected, resExc.getMessage());

        verify(userRepository).existsById(firstId);
        verify(userRepository).existsById(notExistId);
        verify(friendsServer, never()).getMutualFriends(anyLong(), anyLong(), any(Pageable.class));
    }

    @Test
    void isUserExists_whenUserExist_thenReturnTrue() {
        //given
        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(true);

        //when
        boolean res = userService.isUserExists(id);

        //then
        assertTrue(res);
    }

    @Test
    void isUserExists_whenUserNotExist_thenReturnFalse() {
        //given
        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(false);

        //when
        boolean res = userService.isUserExists(id);

        //then
        assertFalse(res);
    }
}