package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception_handler.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.create(user);
    }

    public User update(Long userId, User user) {
        return userRepository.update(userId, user);
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User getUserById(Long userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не существует"));
    }

    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

}
