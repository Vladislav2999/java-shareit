package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();
    UserDto create(UserDto user);

    UserDto update(UserDto user, Long userId);

    UserDto getById(Long userId);

    void delete(Long userId);

}
