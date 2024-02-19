package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartTimeDesc(long userId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :userId " +
            "AND b.startTime < CURRENT_TIMESTAMP " +
            "AND b.endTime > CURRENT_TIMESTAMP " +
            "ORDER BY b.startTime DESC")
    List<Booking> findCurrentBookingsByBooker(
            @Param("userId") long userId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :userId " +
            "AND b.startTime > CURRENT_TIMESTAMP " +
            "ORDER BY b.startTime DESC")
    List<Booking> findFutureBookingsByBooker(@Param("userId") long userId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :userId " +
            "AND b.endTime < CURRENT_TIMESTAMP " +
            "ORDER BY b.startTime DESC")
    List<Booking> findPastBookingsByBooker(@Param("userId") long userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartTimeDesc(long userId, BookingStatus status);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :userId " +
            "ORDER BY b.startTime DESC")
    List<Booking> findAllBookingsByOwner(@Param("userId") long userId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.startTime < CURRENT_TIMESTAMP " +
            "AND b.endTime > CURRENT_TIMESTAMP " +
            "ORDER BY b.startTime DESC")
    List<Booking> findCurrentBookingsByOwner(@Param("userId") long userId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.startTime > CURRENT_TIMESTAMP " +
            "ORDER BY b.startTime DESC")
    List<Booking> findFutureBookingsByOwner(@Param("userId") long userId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.endTime < CURRENT_TIMESTAMP " +
            "ORDER BY b.startTime DESC")
    List<Booking> findPastBookingsByOwner(@Param("userId") long userId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = :userId " +
            "AND b.status = :status " +
            "ORDER BY b.startTime DESC")
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(
            @Param("userId") long userId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status NOT LIKE :status")
    List<Booking> findAllByItemIdAndStatusNotLike(Long itemId, BookingStatus status);


    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.id IN :itemsId " +
            "AND b.status NOT LIKE :status")
    List<Booking> findAllByItemIdInAndStatusNotLike(
            @Param("itemsId") List<Long> itemsId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.endTime < CURRENT_TIMESTAMP " +
            "AND b.status = :status")
    List<Booking> findPastBookingsByBookerAndItemAndStatus(@Param("userId") long userId,
                                                           @Param("itemId") Long itemId,
                                                           @Param("status") BookingStatus status);
}
