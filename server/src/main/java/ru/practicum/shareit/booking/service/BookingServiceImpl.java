package ru.practicum.shareit.booking.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptionHandler.exception.BookingException;
import ru.practicum.shareit.exceptionHandler.exception.CommentException;
import ru.practicum.shareit.exceptionHandler.exception.WrongBookingStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exceptionHandler.exception.EntityNotFoundException;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@Builder
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Primary
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    private final UserService userService;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");




    @Transactional
    @Override
    public BookingDtoOut create(BookingDtoOut bookingDtoOut, Long userId) {
        if (bookingDtoOut.getEnd().isBefore(bookingDtoOut.getStart()) ||
                bookingDtoOut.getEnd().isEqual(bookingDtoOut.getStart())) {
            throw new CommentException("end не может быть раньше start");
        }
        User booker = getUser(userId);
        Item item = getItem(bookingDtoOut.getId());
        if (booker.getId().equals(item.getOwner().getId())) {
            throw new EntityNotFoundException("Owner не может забронировать собственный item");
        }
        Booking booking = Booking.builder()
                .start(bookingDtoOut.getStart())
                .end(bookingDtoOut.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        if (item.getAvailable()) {
            return bookingMapper.toBookingDtoOut(bookingRepository.save(booking));
        } else {
            throw new WrongBookingStateException("item недоступен");
        }
    }

    @Override
    @Transactional
    public BookingDtoOut update(Long bookingId, Long userId, Boolean approved) {
        log.info("Запрос обновления статуса бронирования от пользователя с id " + userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено"));
        if (userId.equals(booking.getItem().getOwner().getId())) {
            if (booking.getStatus() == Status.APPROVED) {
                throw new BookingException("Бронирование уже подтверждено");
            }
            if (isTrue(approved)) {
                booking.setStatus(Status.APPROVED);
            } else if (isFalse(approved)) {
                booking.setStatus(Status.REJECTED);
            } else {
                throw new ValidationException("Ошибка в параметре approved при обновлении бронирования");
            }
            return bookingMapper.toBookingDtoOut(booking);
        } else {
            throw new EntityNotFoundException("Пользователь с id " + userId + "не является владельцем");
        }
    }

    @Override
    public BookingDtoOut getById(Long bookingId, Long userId) {
        log.info("Запрос информации о бронировании с id " + bookingId + " от пользователя с id " + userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование с id " + bookingId + " не найдено"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Пользователь не относится к сделке");
        }
        return bookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> getByOwner(Long id, String state, int from, int size) {
        log.info("Запрос списка бронирований у владельца с id " + id + " с состоянием " + state);
        List<Booking> bookings = Collections.emptyList();
        userService.getById(id);
        Pageable page = PageRequest.of((from / size), size, sort);
        if (EnumUtils.isValidEnum(State.class, state)) {
            switch (State.valueOf(state)) {
                case ALL:
                    bookings = bookingRepository.findByItemOwnerId(id, page);
                    break;
                case CURRENT:
                    LocalDateTime currentTime = LocalDateTime.now();
                    bookings = bookingRepository.findByItemOwnerIdAndEndIsAfterAndStartIsBefore(
                            id,
                            currentTime,
                            currentTime,
                            page
                    );
                    break;
                case PAST:
                    bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(id, LocalDateTime.now(), page);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(id, LocalDateTime.now(), page);
                    break;
                case WAITING:
                    bookings = bookingRepository.findByItemOwnerIdAndStatus(id, Status.WAITING, page);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findByItemOwnerIdAndStatus(id, Status.REJECTED, page);
                    break;
            }
        } else throw new WrongBookingStateException("Ошибка в параметре состояния");

        return bookings.stream().map(bookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOut> getByBooker(Long id, String state, int from, int size) {
        log.info("Запрос списка бронирований у арендатора с id " + id + " с состоянием " + state);
        List<Booking> bookings = Collections.emptyList();
        userService.getById(id);
        Pageable page = PageRequest.of((from / size), size, sort);

        if (EnumUtils.isValidEnum(State.class, state)) {
            switch (State.valueOf(state)) {
                case ALL:
                    bookings = bookingRepository.findByBookerId(id, page);
                    break;
                case CURRENT:
                    LocalDateTime currentTime = LocalDateTime.now();
                    bookings = bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(
                            id,
                            currentTime,
                            currentTime,
                            page);
                    break;
                case PAST:
                    bookings = bookingRepository.findByBookerIdAndEndIsBefore(id, LocalDateTime.now(), page);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByBookerIdAndStartIsAfter(id, LocalDateTime.now(), page);
                    break;
                case WAITING:
                    bookings = bookingRepository.findByBookerIdAndStatus(id, Status.WAITING, page);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findByBookerIdAndStatus(id, Status.REJECTED, page);
                    break;
            }
        } else throw new WrongBookingStateException("Ошибка в параметре состояния");

        return bookings.stream().map(bookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("бронирование не найдено"));
    }

    private User getUser(Long itemId) {
        return userRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("User id=%s не найден"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item id=%s не найден"));
    }

    private List<BookingDtoOut> mapListToDto(Page<Booking> bookingList) {
        return bookingList.stream()
                .map(bookingMapper:: toBookingDtoOut)
                .collect(Collectors.toList());
    }

}