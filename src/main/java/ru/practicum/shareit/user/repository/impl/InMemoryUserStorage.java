package ru.practicum.shareit.user.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception_handler.exception.DuplicateException;
import ru.practicum.shareit.exception_handler.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        if (users.values().stream().filter(u -> Objects.equals(u.getEmail(), user.getEmail())).findAny().isEmpty()) {
            user.setId(++id);
            users.put(user.getId(), user);
            log.info("Создан пользователь с email - " + user.getEmail());
            return user;
        } else {
            throw new DuplicateException("email уже используется");
        }
    }

    @Override
    public User update(Long userId, User user) {
        if (users.get(userId) != null) {
            patchUser(users.get(userId), user);
            log.info("Пользователь с id - " + userId + " обновлен");
            return users.get(userId);
        } else {
            throw new EntityNotFoundException("Пользователь для обновления не найден");
        }
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long userId) {
        log.info("Пользователь с id - " + userId + " удален");
        users.remove(userId);
    }

    private User findByEmail(String email) {
        return users.values().stream().filter(u -> email.equals(u.getEmail()))
                .findAny()
                .orElse(null);
    }

    private void patchUser(User oldUser, User newUser) {
        if (newUser.getEmail() != null) {
            if (users.values().stream().anyMatch(u -> u.getEmail().equals(newUser.getEmail()))
                    && findByEmail(newUser.getEmail()).getId() != oldUser.getId()) { // если email занят пользователем с другим id
                throw new DuplicateException("email уже используется");
            } else {
                oldUser.setEmail(newUser.getEmail());
            }
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        users.put(oldUser.getId(), oldUser);
    }

}
