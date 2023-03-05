package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception_handler.exception.CommentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public CommentDto save(CommentDto commentDto, Long itemId, Long userId) {
        log.info("Запрос на добавление комментария к вещи с id " + itemId + " от пользователя с id " + userId);
        Item item = itemService.getItemById(itemId);
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBefore(
                        userId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .collect(Collectors.toList());
        if (bookings.size() != 0) {
            Comment comment = commentRepository.save(CommentMapper.toComment(commentDto));
            comment.setAuthor(bookings.get(0).getBooker());
            comment.setItem(item);
            return CommentMapper.toCommentDto(comment);
        } else {
            throw new CommentException("Не обнаружено подтвержденный бронирований у вещи " + itemId);
        }
    }
}
