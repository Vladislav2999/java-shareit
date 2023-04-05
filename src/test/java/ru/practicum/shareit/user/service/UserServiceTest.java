package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptionHandler.exception.EntityNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    @InjectMocks
    UserServiceImpl userService;

    User userTest2 = User.builder()
            .email("email@email.ru")
            .name("testUser")
            .id(1L)
            .build();

    @Test
    void createUserCorrectTest() {
        UserDto userTest = UserDto.builder()
                .email("email@email.ru")
                .name("testUser")
                .id(2L)
                .build();

        when(userMapper.toUser(userTest)).thenReturn(userTest2);

        userService.create(userTest);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(userTest.getName(), savedUser.getName());
        assertEquals(userTest.getEmail(), savedUser.getEmail());
    }

    @Test
    void getUserByIdUserNotFoundThrowUserUserNotFoundException() {
        Long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getById(userId));
    }

    @Test
    void getUserByIdUserFoundTest() {
        Long userId = 0L;

        UserDto userDtoTest = UserDto.builder()
                .email("email@email.ru")
                .name("testUser")
                .id(1L)
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(userTest2));
        when(userMapper.toUserDto(any())).thenReturn(userDtoTest);

        assertEquals(userTest2.getId(), userService.getById(userId).getId());
    }

    @Test
    void updateUserUserNotFound() {
        Long userId = 2L;
        UserDto testUserDto = UserDto.builder()
                .email("email@email.ru")
                .name("testUser")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(testUserDto, userId));
    }

    @Test
    void updateUserCorrect() {
        Long userId = 1L;
        User testUser = User.builder()
                .id(1L)
                .email("email@email.ru")
                .name("testUser")
                .build();
        UserDto testUserDto = UserDto.builder()
                .email("email@email.ru")
                .name("testUser1")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(testUser));

        userService.update(testUserDto, userId);

        verify(userMapper).toUserDto(userArgumentCaptor.capture());
        User updatedUser = userArgumentCaptor.getValue();

        assertEquals(testUserDto.getName(), updatedUser.getName());
    }

    @Test
    void deleteUserTest() {
        Long userId = 0L;

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}