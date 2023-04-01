package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptionHandler.exception.DuplicateException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok().body(userService.create(userMapper.toUser(userDto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        if (userId > 0) {
            userDto.setId(userId);

            return ResponseEntity.ok().body(userService.update(userId,userDto));
        } else {
            throw new DuplicateException("Пользователь не существует.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") Long userId) {

        return ResponseEntity.ok().body(userService.getById(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok().body(userService.getAll().stream().map(userMapper::toUserDto)
                .collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") Long userId) {
        userService.deleteById(userId);
    }
}
