package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final UserService userService;

    private UserDto updateUser;

    private User user;

    private User anotherUser;


    @BeforeEach
    void beforeEach() {
        user = new User(null, "testName", "test@mail.com");

        anotherUser = new User(null, "secondName", "secondtest@mail.com");

        updateUser = new UserDto(null, "testNameUpdate", "testUpdate@mail.com");
    }

    @Test
    void createTest() {
        User savedUser = userService.create(user);

        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(user.getName(), savedUser.getName());
        Assertions.assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void update() {
        User savedUser = userService.create(user);
        userService.update(savedUser.getId(), updateUser);

        User actualUser = userService.getById(savedUser.getId());

        Assertions.assertNotNull(actualUser);
        Assertions.assertEquals(updateUser.getName(), actualUser.getName());
        Assertions.assertEquals(updateUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void getById() {
        User savedUser = userService.create(user);
        User foundUser = userService.getById(savedUser.getId());

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(savedUser.getId(), foundUser.getId());
        Assertions.assertEquals(savedUser.getName(), foundUser.getName());
        Assertions.assertEquals(savedUser.getEmail(), foundUser.getEmail());
    }

    @Test
    void getAll() {
        User firstUser = userService.create(user);
        User secondUser = userService.create(anotherUser);

        List<User> foundUsers = userService.getAll();

        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        assertEquals(firstUser.getId(), foundUsers.get(0).getId());
        assertEquals(secondUser.getId(), foundUsers.get(1).getId());
    }

    @Test
    void deleteById() {
        User firstUser = userService.create(user);
        User secondUser = userService.create(anotherUser);
        userService.deleteById(firstUser.getId());

        List<User> foundUsers = userService.getAll();

        assertEquals(1, foundUsers.size());
        assertEquals(secondUser.getId(), foundUsers.get(0).getId());
        assertEquals(secondUser.getName(), foundUsers.get(0).getName());
        assertEquals(secondUser.getEmail(), foundUsers.get(0).getEmail());
    }
}
