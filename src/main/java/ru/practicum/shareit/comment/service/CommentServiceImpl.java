package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptionHandler.exception.CommentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private static final int FIRST_PAGE = 0;

    private static final int MAX_SIZE = 10000;

    private final CommentRepository commentRepository;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public CommentDto create(CommentDto commentDto, Long itemId, Long userId) {
        log.info("Запрос на добавление комментария к вещи с id " + itemId + " от пользователя с id " + userId);
        Item item = itemService.getItemById(itemId);
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(FIRST_PAGE, MAX_SIZE,  Sort.by(DESC, "start")))
                .stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .collect(Collectors.toList());
        if (bookings.size() != 0) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setAuthor(bookings.get(0).getBooker());
            comment.setItem(item);
            commentRepository.save(comment);
            return CommentMapper.toCommentDto(comment);
        } else {
            throw new CommentException("Не обнаружено подтвержденных бронирований у вещи " + itemId);
        }
    }
}
