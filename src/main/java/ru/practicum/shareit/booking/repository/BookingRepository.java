package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(Long userId, Status status, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndIsAfterAndStartIsBefore(Long ownerId,
                                                                 LocalDateTime time,
                                                                 LocalDateTime sameTime,
                                                                 Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime time, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime time, Pageable pageable);

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(Long bookerId,
                                                              LocalDateTime time,
                                                              LocalDateTime sameTime,
                                                              Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfter(Long userId, LocalDateTime time, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime time, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long userId, Status status, Pageable pageable);

    List<Booking> findByItemInAndStatus(List<Item> items, Status status, Sort sort);

    Booking findByEndIsBeforeAndItemOwnerIdAndItemId(LocalDateTime now, Long userId, Long itemId, Sort sort);

    Booking findByStartIsAfterAndItemOwnerIdAndItemId(LocalDateTime now, Long userId, Long itemId, Sort sort);

}
