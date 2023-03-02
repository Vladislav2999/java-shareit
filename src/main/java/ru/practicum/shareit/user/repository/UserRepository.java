package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    User update(Long userId, User user);

    Optional<User> getUserById(Long userId);

    List<User> getAll();

    void deleteById(Long userId);

}