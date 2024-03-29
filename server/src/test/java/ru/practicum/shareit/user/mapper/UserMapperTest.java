package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void toUserDtoTest() {
        User user = new User(1L, "Имя", "email@email.com");
        UserDto userDto = userMapper.toUserDto(user);

        assertEquals(1, userDto.getId());
        assertEquals("Имя", userDto.getName());
        assertEquals("email@email.com", userDto.getEmail());
    }

    @Test
    public void toUserTest() {
        UserDto userDto = new UserDto(1L, "Имя", "email@email.com");
        User user = userMapper.toUser(userDto);

        assertEquals(1, user.getId());
        assertEquals("Имя", user.getName());
        assertEquals("email@email.com", user.getEmail());
    }
}
