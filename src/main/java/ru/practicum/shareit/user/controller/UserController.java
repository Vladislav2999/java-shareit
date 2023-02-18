package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception_handler.exception.DuplicateException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        return UserMapper.toUserDto(userService.create(UserMapper.toUser(userDto)));
    }
    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        if (userId > 0) {
            userDto.setId(userId);
            return UserMapper.toUserDto(userService.update(userId, UserMapper.toUser(userDto)));
        } else {
            throw new DuplicateException("Пользователь не существует.");
        }
    }
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable("id") Long userId) {
        return UserMapper.toUserDto(userService.getUserById(userId));
    }
    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") Long userId) {
        userService.deleteById(userId);
    }
}
