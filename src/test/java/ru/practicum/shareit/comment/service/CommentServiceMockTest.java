package ru.practicum.shareit.comment.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptionHandler.exception.CommentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceMockTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private ItemService itemService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    private User booker;

    private Item item;

    private Booking booking;

    private Comment comment;

    private CommentDto commentDto;

    @BeforeEach
    void beforeEach() {
        LocalDateTime commentTime = LocalDateTime.now().minusHours(1);
        User owner = new User(1L, "owner", "owner@mail.ru");
        booker = new User(2L, "booker", "booker@mail.ru");
        item = new Item(1L, "name", "description", true, owner);
        comment = new Comment(1L, "text", item, booker, commentTime);
        commentDto = new CommentDto(1L, "text", "booker", commentTime);
        booking = new Booking(
                1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item,
                booker,
                Status.APPROVED);
    }

    @Test
    void createTest() {
        when(bookingRepository.findByBookerIdAndEndIsBefore(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        CommentDto savedComment = commentService.create(commentDto, item.getId(), booker.getId());

        assertNotNull(savedComment);
        assertNotNull(savedComment.getCreated());
        assertEquals(commentDto.getText(), savedComment.getText());
        assertEquals(commentDto.getAuthorName(), savedComment.getAuthorName());
        assertEquals(commentDto.getCreated(), savedComment.getCreated());
    }

    @Test
    void createTestWithNoApprovedBookings() {
        when(bookingRepository.findByBookerIdAndEndIsBefore(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());
        when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        Exception exception = Assertions.assertThrows(CommentException.class,
                () -> commentService.create(commentDto, item.getId(), booker.getId()));

        Assertions.assertEquals("Не обнаружено подтвержденных бронирований у вещи 1",
                exception.getMessage());
    }

}
