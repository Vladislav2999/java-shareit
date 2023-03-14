package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long userId, Status status, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsAfterAndStartIsBefore(Long ownerId,
                                                                 LocalDateTime time,
                                                                 LocalDateTime sameTime,
                                                                 Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime time, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(Long bookerId,
                                                              LocalDateTime time,
                                                              LocalDateTime sameTime,
                                                              Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long userId, Status status, Sort sort);

    List<Booking> findByItemInAndStatus(List<Item> items, Status status, Sort sort);

    Booking findByEndIsBeforeAndItemOwnerIdAndItemId(LocalDateTime now, Long userId, Long itemId, Sort sort);

    Booking findByStartIsAfterAndItemOwnerIdAndItemId(LocalDateTime now, Long userId, Long itemId, Sort sort);

}
