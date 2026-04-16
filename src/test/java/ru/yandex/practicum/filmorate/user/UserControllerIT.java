package ru.yandex.practicum.filmorate.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.user.model.dto.UserDto;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto firstDto;
    private UserDto secondDto;
    private UserDto mutualFriend;

    @BeforeEach
    void setUp() {
        firstDto = new UserDto();
        firstDto.setName("firstDto");
        firstDto.setEmail("firstDto@mail.ru");
        firstDto.setLogin("firstDto");
        firstDto.setBirthday(LocalDate.of(1980, 1, 1));

        secondDto = new UserDto();
        secondDto.setName("secondDto");
        secondDto.setLogin("secondDto");
        secondDto.setEmail("secondDto@gmail.com");
        secondDto.setBirthday(LocalDate.of(1980, 1, 1));

        mutualFriend = new UserDto();
        mutualFriend.setName("mutualFriend");
        mutualFriend.setLogin("mutualFriend");
        mutualFriend.setEmail("mutualFriend@gmail.com");
        mutualFriend.setBirthday(LocalDate.of(1980, 1, 1));
    }

    @SneakyThrows
    @Test
    void add_whenRequestIsValid_thenCreatedAndReturnUserDto() {
        //when+then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(firstDto.getName()))
                .andExpect(jsonPath("$.login").value(firstDto.getLogin()))
                .andExpect(jsonPath("$.email").value(firstDto.getEmail()))
                .andExpect(jsonPath("$.birthday").value(firstDto.getBirthday().toString()));
    }

    @SneakyThrows
    @Test
    void add_whenNameEmpty_thenSubstituteValueFromLoginIntoNameAndCreatedAndReturnUserDto() {
        //given
        firstDto.setName("");

        //when+then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(firstDto.getLogin())) //когда у dto пустой name он заполняется из поля login
                .andExpect(jsonPath("$.login").value(firstDto.getLogin()));
    }

    @SneakyThrows
    @Test
    void add_whenLoginEmpty_thenReturn400() {
        //given
        firstDto.setLogin("");

        //when+then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenEmailEmpty_thenReturn400() {
        //given
        firstDto.setEmail("");

        //when+then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenEmailNotValid_thenReturn400() {
        //given
        firstDto.setEmail("неверный формат");

        //when+then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenBirthdayEmpty_thenReturn400() {
        //given
        firstDto.setBirthday(null);

        //when+then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void add_whenBirthdayFuture_thenReturn400() {
        //given
        firstDto.setBirthday(LocalDate.now().plusDays(1));

        //when+then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenRequestIsValid_thenUpdateAndReturnUserDto() {
        //given
        Long userId = addUser(firstDto);

        secondDto.setId(userId);

        //when+then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(secondDto.getName()))
                .andExpect(jsonPath("$.login").value(secondDto.getLogin()))
                .andExpect(jsonPath("$.email").value(secondDto.getEmail()))
                .andExpect(jsonPath("$.birthday").value(secondDto.getBirthday().toString()));
    }

    @SneakyThrows
    @Test
    void update_whenNameEmpty_thenSubstituteValueFromLoginIntoNameAndUpdateAndReturn200AndUserDto() {
        //given
        Long userId = addUser(firstDto);

        secondDto.setId(userId);
        secondDto.setName("");

        //when+then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(secondDto.getLogin())) //когда у dto пустой name он заполняется из поля login
                .andExpect(jsonPath("$.login").value(secondDto.getLogin()));
    }

    @SneakyThrows
    @Test
    void update_whenIdNull_thenReturn400() {
        //given
        addUser(firstDto);

        secondDto.setId(null);

        //when+then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto))) // secondDto.id == null
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenUserNotExist_thenReturn404() {
        //given
        secondDto.setId(777L);

        //when+then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void update_whenLoginEmpty_thenReturn400() {
        //given
        Long userId = addUser(firstDto);
        secondDto.setId(userId);

        secondDto.setLogin("");

        //when+then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenEmailEmpty_thenReturn400() {
        //given
        Long userId = addUser(firstDto);
        secondDto.setId(userId);

        secondDto.setEmail("");

        //when+then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenEmailNotValid_thenReturn400() {
        //given
        Long userId = addUser(firstDto);
        secondDto.setId(userId);

        secondDto.setEmail("неверный формат");

        //when+then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenBirthdayEmpty_thenReturn400() {
        //given
        Long userId = addUser(firstDto);
        secondDto.setId(userId);

        secondDto.setBirthday(null);

        //when+then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void update_whenBirthdayFuture_thenReturn400() {
        //given
        Long userId = addUser(firstDto);
        secondDto.setId(userId);

        secondDto.setBirthday(LocalDate.now().plusDays(1));

        //when+then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void delete_whenUserExist_thenRemoveUser() {
        //given
        Long id = addUser(firstDto);

        //when
        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isOk());

        //then
        //getAllUsers
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void delete_whenUserNotExist_thenReturn404() {
        //given
        Long id = 7777L;

        //when
        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getById_whenUserExist_thenReturnUser() {
        //given
        Long id = addUser(firstDto);

        //when+then
        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(firstDto.getName()))
                .andExpect(jsonPath("$.login").value(firstDto.getLogin()))
                .andExpect(jsonPath("$.email").value(firstDto.getEmail()))
                .andExpect(jsonPath("$.birthday").value(firstDto.getBirthday().toString()));
    }

    @SneakyThrows
    @Test
    void getById_whenUserNotExist_thenReturn404() {
        //given
        Long id = 7777L;

        //when
        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersExist_thenReturn200AndListOfUsers() {
        //given
        addUser(firstDto);

        //when+then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(firstDto.getName()))
                .andExpect(jsonPath("$[0].login").value(firstDto.getLogin()))
                .andExpect(jsonPath("$[0].email").value(firstDto.getEmail()))
                .andExpect(jsonPath("$[0].birthday").value(firstDto.getBirthday().toString()));
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersNotExist_thenReturn200AndListEmpty() {
        //when+then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void addFriend_whenRequestIsValid_thenAddFriendAndReturn200() {
        //given
        Long firstId = addUser(firstDto);
        Long secondId = addUser(secondDto);

        //when
        mockMvc.perform(put("/users/{id}/friends/{friendId}", firstId, secondId))
                .andExpect(status().isOk());

        //then
        //getFriends
        //проверим что дружба между firstId и secondId сохранена
        mockMvc.perform(get("/users/{id}/friends", firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(secondId));

        //заодно проверим что дружба в одну сторону
        mockMvc.perform(get("/users/{id}/friends", secondId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void addFriend_whenFriendshipIsConfirmed_thenAddFriendAndReturn200() {
        //given
        Long firstId = addUser(firstDto);
        Long secondId = addUser(secondDto);

        addFriendship(firstId, secondId);

        //when
        // подтверждаем дружбу

        mockMvc.perform(put("/users/{id}/friends/{friendId}", secondId, firstId))
                .andExpect(status().isOk());

        //then
        //getFriends
        //оба пользователя друг у друга в друзьях
        mockMvc.perform(get("/users/{id}/friends", secondId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(firstId));

        mockMvc.perform(get("/users/{id}/friends", firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(secondId));
    }

    @SneakyThrows
    @Test
    void addFriend_whenAddingDuplicateFriend_thenReturn400() {
        //given
        Long firstId = addUser(firstDto);
        Long secondId = addUser(secondDto);

        addFriendship(firstId, secondId);

        //when+then
        mockMvc.perform(put("/users/{id}/friends/{friendId}", firstId, secondId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addFriend_whenUserAddsHimselfFriend_thenReturn400() {
        //given
        Long firstId = addUser(firstDto);

        //when+then
        //сам себя добавляет
        mockMvc.perform(put("/users/{id}/friends/{friendId}", firstId, firstId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void addFriend_whenUserAddsNotExistingFriend_thenReturn404() {
        //given
        Long firstId = addUser(firstDto);
        Long notExistId = 777L;

        //when+then
        mockMvc.perform(put("/users/{id}/friends/{friendId}", firstId, notExistId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void addFriend_whenNotExistingUserAddsFriend_thenReturn404() {
        //given
        Long notExistId = 777L;
        Long friendId = addUser(firstDto);

        //when+then
        mockMvc.perform(put("/users/{id}/friends/{friendId}", notExistId, friendId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void removeFriend_whenRequestIsValid_thenRemoveFriendAndReturn200() {
        //given
        Long firstId = addUser(firstDto);
        Long secondId = addUser(secondDto);

        addFriendship(firstId, secondId);
        addFriendship(secondId, firstId);

        //when
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", firstId, secondId))
                .andExpect(status().isOk());

        //then
        //getFriends
        //проверим что дружба между firstId и secondId удалена
        mockMvc.perform(get("/users/{id}/friends", firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        //заодно проверим что дружба удалена только в одну сторону
        mockMvc.perform(get("/users/{id}/friends", secondId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(firstId));
    }

    @SneakyThrows
    @Test
    void removeFriend_whenUserDeletesHimselfFromFriends_thenReturn400() {
        //given
        Long firstId = addUser(firstDto);

        //when+then
        //сам себя удаляет из своих друзей
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", firstId, firstId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void removeFriend_whenUserDeletesNotExistingFriend_thenReturn404() {
        //given
        Long firstId = addUser(firstDto);
        Long notExistId = 777L;

        //when+then
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", firstId, notExistId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void removeFriend_whenNotExistingUserDeletesFriend_thenReturn404() {
        //given
        Long notExistId = 777L;
        Long friendId = addUser(firstDto);

        //when+then
        mockMvc.perform(delete("/users/{id}/friends/{friendId}", notExistId, friendId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getFriends_whenExistFriends_thenReturn200AndListOfUsers() {
        //given
        Long firstId = addUser(firstDto);
        Long secondId = addUser(secondDto);

        addFriendship(firstId, secondId);

        //when+then
        mockMvc.perform(get("/users/{id}/friends", firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(secondId))
                .andExpect(jsonPath("$[0].name").value(secondDto.getName()))
                .andExpect(jsonPath("$[0].login").value(secondDto.getLogin()))
                .andExpect(jsonPath("$[0].email").value(secondDto.getEmail()))
                .andExpect(jsonPath("$[0].birthday").value(secondDto.getBirthday().toString()));
    }

    @SneakyThrows
    @Test
    void getFriends_whenNotExistFriends_thenReturn200AndEmptyList() {
        //given
        Long firstId = addUser(firstDto);
        addUser(secondDto);

        //when+then
        mockMvc.perform(get("/users/{id}/friends", firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void getFriends_whenNotExistUser_thenReturn404() {
        //given
        Long nonExistId = 777L;

        //when+then
        mockMvc.perform(get("/users/{id}/friends", nonExistId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getMutualFriends_whenExistMutualFriends_thenReturn200AndListOfUsers() {
        //given
        Long firstId = addUser(firstDto);
        Long secondId = addUser(secondDto);
        Long mutualId = addUser(mutualFriend);

        addFriendship(firstId, mutualId);
        addFriendship(secondId, mutualId);

        //when+then
        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", firstId, secondId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(mutualId))
                .andExpect(jsonPath("$[0].name").value(mutualFriend.getName()))
                .andExpect(jsonPath("$[0].login").value(mutualFriend.getLogin()))
                .andExpect(jsonPath("$[0].email").value(mutualFriend.getEmail()))
                .andExpect(jsonPath("$[0].birthday").value(mutualFriend.getBirthday().toString()));
    }

    @SneakyThrows
    @Test
    void getMutualFriends_whenNotExistMutualFriends_thenReturn200AndEmptyList() {
        //given
        Long firstId = addUser(firstDto);
        Long secondId = addUser(secondDto);
        Long mutualId = addUser(mutualFriend);

        //when+then
        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", firstId, secondId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void getMutualFriends_whenUserRequestCommonFriendsHimself_thenReturn400() {
        //given
        Long firstId = addUser(firstDto);

        //when+then
        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", firstId, firstId))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getMutualFriends_whenFirstUserNotExist_thenReturn404() {
        //given
        Long notExistId = 777L;
        Long secondId = addUser(secondDto);

        //when+then
        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", notExistId, secondId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getMutualFriends_whenSecondUserNotExist_thenReturn404() {
        //given
        Long firstId = addUser(firstDto);
        Long notExistId = 777L;

        //when+then
        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", firstId, notExistId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getEvents_whenEventNotExist_thenReturn200AndEmptyList() {
        //given
        Long firstId = addUser(firstDto);

        //when+then
        mockMvc.perform(get("/users/{id}/feed", firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void getEvents_whenEventExist_thenReturn200AndListOfUsers() {
        //given
        Long firstId = addUser(firstDto);
        Long secondId = addUser(secondDto);

        //создаем событие
        addFriendship(firstId, secondId);

        //when+then
        mockMvc.perform(get("/users/{id}/feed", firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void getEvents_whenNotExistUser_thenReturn404() {
        //given
        Long notExistId = 777L;

        //when+then
        mockMvc.perform(get("/users/{id}/feed", notExistId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    private Long addUser(UserDto userDto) {
        String jsonRes = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto userRes = objectMapper.readValue(jsonRes, UserDto.class);
        return userRes.getId();
    }

    @SneakyThrows
    private void addFriendship(Long firstId, Long secondId) {
        mockMvc.perform(put("/users/{id}/friends/{friendId}", firstId, secondId))
                .andExpect(status().isOk());
    }
}