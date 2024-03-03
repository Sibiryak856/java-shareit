package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartTimeBeforeAndEndTimeAfter(
            long userId, LocalDateTime now, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartTimeAfter(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndTimeBefore(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerId(long userId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfter(
            long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findFAllByItemOwnerIdAndStartTimeAfter(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndTimeBefore(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndItemIdAndStatusIsAndEndTimeBefore(
            Long userId, Long itemId, BookingStatus status, LocalDateTime now);

    List<Booking> findAllByItemIdAndStatusIs(
            Long itemId, BookingStatus bookingStatus, Sort sort);

    List<Booking> findAllByItemIdInAndStatusIs(List<Long> itemsId, BookingStatus bookingStatus);
}
