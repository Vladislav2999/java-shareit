package ru.practicum.shareit.comment.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@ActiveProfiles("test")
public class CommentMapperTest {

    private final LocalDateTime commentTime = LocalDateTime.now().minusDays(1);

    @Autowired
    private CommentMapper commentMapper;

    @Test
    void toCommentTest() {
        CommentDto commentDto = new CommentDto(1L, "text", "Name", commentTime);

        Comment comment = commentMapper.toComment(commentDto);

        assertNotNull(comment);
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getCreated(), comment.getCreated());
    }

    @Test
    void toCommentDtoTest() {
        User owner = new User(1L, "name", "email@mail.ru");
        User commentator = new User(2L, "commentatorName", "commentator@mail.ru");
        Item item = new Item(1L, "name", "description", true, owner);
        Comment comment = new Comment(1L, "text", item, commentator, commentTime);

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertNotNull(comment);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}
