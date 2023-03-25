package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptionHandler.exception.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.dto.ItemDtoOut;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final RequestRepository requestRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDtoOut create(ItemDtoIn itemDto, Long userId) {
        log.info("Запрос создания новой вещи от пользователя с id " + userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = ItemMapper.toItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new EntityNotFoundException("Запрос вещи, переданный в параметре не найден"));
            item.setRequest(itemRequest);
        }
        Item savedItem = itemRepository.save(item);
        savedItem.setOwner(user);
        return ItemMapper.toItemDtoOut(savedItem);
    }

    @Override
    @Transactional
    public ItemDtoOut update(ItemDtoIn itemDto, Long userId) {
        log.info("Запрос обновления вещи от пользователя с id " + userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + " не найден"));
        Item oldItem = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("вещь с id " + itemDto.getId() + " не найдена"));
        Item newItem = ItemMapper.toItem(itemDto, user);
        if (oldItem.getOwner().getId().equals(userId)) {
            patchItem(oldItem, newItem);
            return getByItemIdAndUserId(userId, oldItem.getId());
        } else {
            throw new EntityNotFoundException("Ошибка обновления вещи: неверный вледелец");
        }
    }

    @Override
    public ItemDtoOut getByItemIdAndUserId(Long userId, Long itemId) {
        log.info("Запрос данных о вещи с id " + itemId + " от пользователя с id " + userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("вещь с id " + itemId + " не найдена"));
        ItemDtoOut itemResponse = ItemMapper.toItemDtoOut(item);

        itemResponse.setComments(commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList()));

        Booking lastBooking = bookingRepository.findByEndIsBeforeAndItemOwnerIdAndItemId(
                LocalDateTime.now(),
                userId,
                itemId,
                Sort.by(Sort.Direction.DESC, "start"));
        Booking nextBooking = bookingRepository.findByStartIsAfterAndItemOwnerIdAndItemId(
                LocalDateTime.now(),
                userId,
                itemId,
                Sort.by(Sort.Direction.DESC, "start"));

        itemResponse.setLastBooking(lastBooking == null ? null : new ItemDtoOut.Booking(
                lastBooking.getId(),
                lastBooking.getBooker().getId()));
        itemResponse.setNextBooking(nextBooking == null ? null : new ItemDtoOut.Booking(
                nextBooking.getId(),
                nextBooking.getBooker().getId()));

        return itemResponse;
    }

    @Override
    public List<ItemDtoOut> getAll(Long userId, int from, int size) {
        log.info("Запрос списка всех вещей от пользователя с id " + userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId, PageRequest.of((from / size), size, Sort.by(ASC, "id")));
        Map<Item, List<Booking>> bookings = bookingRepository.findByItemInAndStatus(
                        items, Status.APPROVED, Sort.by(DESC, "start"))
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        Map<Item, List<Comment>> comments = commentRepository.findAllByItemIn(items,
                        Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        List<ItemDtoOut> itemDtoList = new ArrayList<>();
        for (Item item : items) {
            ItemDtoOut itemWithBooking = addBookings(item,
                    bookings.getOrDefault(item, Collections.emptyList()));
            ItemDtoOut itemWithCommentsAndBooking = addComments(itemWithBooking,
                    comments.getOrDefault(item, Collections.emptyList()));
            itemDtoList.add(itemWithCommentsAndBooking);
        }
        return itemDtoList;
    }

    @Override
    public List<ItemDtoOut> getByText(String text, Long userId, int from, int size) {
        log.info("Запрос поиска по назвнию или описанию вещи от пользователя с id " + userId);
        if (StringUtils.isBlank(text)) {
            log.info("В параметр поиска была передана пустая строка");
            return Collections.emptyList();
        }
        return itemRepository.search(text, PageRequest.of((from / size), size)).stream()
                .map(ItemMapper::toItemDtoOut)
                .collect(toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь не сущестует"));
    }

    public ItemDtoOut addBookings(Item item, List<Booking> bookings) {
        ItemDtoOut itemDto = ItemMapper.toItemDtoOut(item);
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookings.stream()
                .filter(b -> ((b.getEnd().isEqual(now) || b.getEnd().isBefore(now))
                        || (b.getStart().isEqual(now) || b.getStart().isBefore(now))))
                .findFirst()
                .orElse(null);

        Booking nextBooking = bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .reduce((first, second) -> second)
                .orElse(null);

        if (lastBooking != null) {
            itemDto.setLastBooking(new ItemDtoOut.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(new ItemDtoOut.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
        }

        return itemDto;
    }

    public ItemDtoOut addComments(ItemDtoOut itemDto, List<Comment> comments) {
        itemDto.setComments(comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList()));

        return itemDto;
    }

    private void patchItem(Item oldItem, Item newItem) {
        Optional.ofNullable(newItem.getName()).ifPresent(oldItem::setName);
        Optional.ofNullable(newItem.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(newItem.getRequest()).ifPresent(oldItem::setRequest);
        Optional.ofNullable(newItem.getAvailable()).ifPresent(oldItem::setAvailable);

        itemRepository.save(oldItem);
    }

}
