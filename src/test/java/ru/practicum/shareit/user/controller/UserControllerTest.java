package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private User user;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "name", "email@mail.ru");
        userDto = new UserDto(1L, "name", "email@mail.ru");
    }

    @Test
    void createTest() throws Exception {
        when(userService.create(Mockito.any(User.class)))
                .thenReturn(user);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(user.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail()), String.class));
    }

    @Test
    void updateTest() throws Exception {
        User userUpdated = new User(1L, "nameUpdated", "updated@mail.ru");
        UserDto userUpdatedDto = new UserDto(1L, "nameUpdated", "updated@mail.ru");

        when(userService.update(Mockito.anyLong(), Mockito.any(UserDto.class)))
                .thenReturn(userUpdated);

        mockMvc.perform(patch("/users/{userId}", user.getId())
                        .content(mapper.writeValueAsString(userUpdatedDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(userUpdated.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userUpdated.getEmail()), String.class));
    }

    @Test
    void getByIdTest() throws Exception {
        when(userService.getById(Mockito.anyLong()))
                .thenReturn(user);

        mockMvc.perform(get("/users/{userId}", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(user.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(user.getEmail()), String.class));
    }

    @Test
    void getAllTest() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(user));

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));

        verify(userService, Mockito.times(1))
                .getAll();
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(userService, Mockito.times(1))
                .deleteById(Mockito.anyLong());
    }

}

