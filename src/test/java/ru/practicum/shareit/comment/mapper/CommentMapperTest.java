package ru.practicum.shareit.comment.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.comment.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.comment.mapper.CommentMapper.toCommentDto;

public class CommentMapperTest {

    private final LocalDateTime commentTime = LocalDateTime.now().minusDays(1);

    @Test
    void toCommentTest() {
        CommentDto commentDto = new CommentDto(1L, "text", "Name", commentTime);

        Comment comment = toComment(commentDto);

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

        CommentDto commentDto = toCommentDto(comment);

        assertNotNull(comment);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}
