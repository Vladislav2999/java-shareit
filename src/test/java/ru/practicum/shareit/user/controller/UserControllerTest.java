package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void addUserValidStatusIsOk() {
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@test.ru")
                .build();
        when(userService.create(any())).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void addUserUserIsNotValidBadRequest() {
        UserDto userDtoToAdd = UserDto.builder().build();
        when(userService.create(userDtoToAdd)).thenReturn(userDtoToAdd);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToAdd)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        verify(userService, never()).create(userDtoToAdd);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        Integer userId = 1;
        UserDto userDto = UserDto.builder()
                .name("test")
                .email("test@test.ru")
                .build();
        when(userService.update(any(), any())).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void getUserById() {
        Long userId = 0L;

        mockMvc.perform(get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getById(userId);
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getAll();
    }

    @SneakyThrows
    @Test
    void deleteUserById() {
        Long userId = 0L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }
}