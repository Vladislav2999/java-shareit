package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exceptionHandler.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class UserServiceMockTest {

    private UserRepository userRepository;

    private UserService userService;

    private User user;

    private User updateUser;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);

        user = new User(1L, "testName", "test@mail.com");
        updateUser = new User(1L, "testNameUpdate", "testUpdate@mail.com");
        userDto = new UserDto(1L, "testNameUpdate", "testUpdate@mail.com");
    }

    @Test
    void findAllTest() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll())
                .thenReturn(users);

        List<User> usersGet = userService.getAll();

        Assertions.assertNotNull(usersGet);
        Assertions.assertEquals(1, usersGet.size());
    }

    @Test
    void findByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        User foundUser = userService.getById(user.getId());

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(user.getId(), foundUser.getId());
        Assertions.assertEquals(user.getName(), foundUser.getName());
        Assertions.assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void findByIdTestWithWrongUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.getById(1L));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }

    @Test
    void saveTest() {
        when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        User savedUser = userService.create(user);

        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(user.getId(), savedUser.getId());
        Assertions.assertEquals(user.getName(), savedUser.getName());
        Assertions.assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void updateTest() {


        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(updateUser);

        User updatedUser = userService.update(user.getId(), userDto);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(updateUser.getId(), updatedUser.getId());
        Assertions.assertEquals(updateUser.getName(), updatedUser.getName());
        Assertions.assertEquals(updateUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateTestWithWrongUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.update(1L, userDto));

        Assertions.assertEquals("Пользователь с id 1 не найден", exception.getMessage());
    }
}
