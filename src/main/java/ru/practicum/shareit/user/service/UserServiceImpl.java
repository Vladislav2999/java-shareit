package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception_handler.exception.DuplicateException;
import ru.practicum.shareit.exception_handler.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(User user) {
        log.info("Запрос создания новго пользователя");
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Long userId, UserDto userDto) {
        log.info("Запрос обновления пользователя с id " + userId);
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + "не найден"));
        if (checkEmail(user, userDto)) {
            Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        } else throw new DuplicateException("email уже используется");
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        log.info("Запрос получения списка всех пользователей");
        return userRepository.findAll();
    }

    @Override
    public User getById(Long userId) {
        log.info("Запрос информации о пользователе с id " + userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользовательс id " + userId + " не найден"));
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        log.info("Запрос удаления пользователя с id " + userId);
        userRepository.deleteById(userId);
    }

    private boolean checkEmail(User user, UserDto userDto) {
        return !user.equals(userRepository.findByEmailIgnoreCase(userDto.getEmail()));
    }
}
