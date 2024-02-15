package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByStartTimeDesc(long userId);

    @Query("SELECT B FROM BOOKINGS AS B " +
            "WHERE B.USER_ID = :userId " +
            "AND B.START_TIME <= CURRENT_TIMESTAMP " +
            "AND B.END_TIME >= CURRENT_TIMESTAMP " +
            "ORDER BY B.START_TIME DESC")
    List<Booking> findCurrentBookingByUser(@Param("USER_ID") long userId);

    List<Booking> findByUserIdAndStartTimeAfterOrderByStartTimeDesc(long userId, LocalDateTime now);

    List<Booking> findByUserIdAndEndTimeBeforeOrderByStartTimeDesc(long userId, LocalDateTime now);


    List<Booking> findBookingByUserIdAndStatusOrderByStartTimeDesc(long userId, String name);

    @Query("SELECT B FROM BOOKINGS AS B " +
            "LEFT JOIN ITEMS AS I ON I.ITEM_ID = B.ITEM_ID " +
            "WHERE I.USER_ID = :userID " +
            "ORDER BY B.START_TIME DESC")
    List<Booking> findAllBookingByItemOwner(@Param("USER_ID") long userId);

    @Query("SELECT B FROM BOOKINGS AS B " +
            "LEFT JOIN ITEMS AS I ON I.ITEM_ID = B.ITEM_ID " +
            "WHERE I.USER_ID = :userId " +
            "AND B.START_TIME <= CURRENT_TIMESTAMP " +
            "AND B.END_TIME >= CURRENT_TIMESTAMP " +
            "ORDER BY B.START_TIME DESC")
    List<Booking> findCurrentBookingByOwner(@Param("USER_ID") long userId);

    @Query("SELECT B FROM BOOKINGS AS B " +
            "LEFT JOIN ITEMS AS I ON I.ITEM_ID = B.ITEM_ID " +
            "WHERE I.USER_ID = :userId " +
            "AND B.START_TIME > CURRENT_TIMESTAMP " +
            "ORDER BY B.START_TIME DESC")
    List<Booking> findFutureBookingByOwner(@Param("USER_ID") long userId);

    @Query("SELECT B FROM BOOKINGS AS B " +
            "LEFT JOIN ITEMS AS I ON I.ITEM_ID = B.ITEM_ID " +
            "WHERE I.USER_ID = :userId " +
            "AND B.END_TIME < CURRENT_TIMESTAMP " +
            "ORDER BY B.START_TIME DESC")
    List<Booking> findPastBookingByOwner(@Param("USER_ID") long userId);

    @Query("SELECT B FROM BOOKINGS AS B " +
            "LEFT JOIN ITEMS AS I ON I.ITEM_ID = B.ITEM_ID " +
            "WHERE I.USER_ID = :userId AND B.STATUS = :status " +
            "ORDER BY B.START_TIME DESC")
    List<Booking> findBookingByOwnerAndStatus(@Param("USER_ID") long userId, @Param("STATUS") String status);

    List<Booking> findByItemIdInAndStatus(List<Long> itemsIdList, String name);
}
