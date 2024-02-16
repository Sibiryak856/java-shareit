package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartTimeDesc(long userId);

    /*@Query("SELECT B FROM Bookings AS B " +
            "WHERE B.USER.ID = :userId " +
            "AND B.START.TIME <= CURRENT_TIMESTAMP " +
            "AND B.END.TIME >= CURRENT_TIMESTAMP " +
            "ORDER BY B.START.TIME DESC")*/
    List<Booking> findAllByBookerIdAndStartTimeBeforeAndEndTimeAfter(@Param("userId") long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStartTimeAfterOrderByStartTimeDesc(long userId, LocalDateTime now);

    List<Booking> findByBookerIdAndEndTimeBeforeOrderByStartTimeDesc(long userId, LocalDateTime now);


    List<Booking> findBookingByBookerIdAndStatusOrderByStartTimeDesc(long userId, String name);

    /*@Query("SELECT B FROM Bookings AS B " +
            "LEFT JOIN Items AS I ON I.ITEM.ID = B.ITEM.ID " +
            "WHERE I.USER.ID = :userID " +
            "ORDER BY B.START.TIME DESC")*/
    List<Booking> findAllByItemOwnerId(@Param("userId") long userId);

    /*@Query("SELECT B FROM Bookings AS B " +
            "LEFT JOIN Items AS I ON I.ITEM.ID = B.ITEM.ID " +
            "WHERE I.USER.ID = :userId " +
            "AND B.START.TIME < CURRENT_TIMESTAMP " +
            "AND B.END.TIME > CURRENT_TIMESTAMP " +
            "ORDER BY B.START.TIME DESC")
    List<Booking> findCurrentBookingByOwner(@Param("userId") long userId);*/
    List<Booking> findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfter(Long userId, LocalDateTime start, LocalDateTime end);

    /*@Query("SELECT B FROM Bookings AS B " +
            "LEFT JOIN Items AS I ON I.ITEM.ID = B.ITEM.ID " +
            "WHERE I.USER.ID = :userId " +
            "AND B.START.TIME > CURRENT_TIMESTAMP " +
            "ORDER BY B.START.TIME DESC")*/
    List<Booking> findAllByItemOwnerIdAndStartTimeAfter(long userId, LocalDateTime now);

    /*@Query("SELECT B FROM Bookings AS B " +
            "LEFT JOIN Items AS I ON I.ITEM.ID = B.ITEM.ID " +
            "WHERE I.USER.ID = :userId " +
            "AND B.END.TIME < CURRENT_TIMESTAMP " +
            "ORDER BY B.START.TIME DESC")*/
    List<Booking> findAllByItemOwnerIdAndEndTimeBefore(long userId, LocalDateTime now);

    /*@Query("SELECT B FROM Bookings AS B " +
            "LEFT JOIN Items AS I ON I.ITEM.ID = B.ITEM.ID " +
            "WHERE I.USER.ID = :userId AND B.STATUS = :status " +
            "ORDER BY B.START.TIME DESC")*/
    List<Booking> findBookingByItemOwnerIdAndStatus(@Param("userId") long userId, @Param("status") String status);

    /**
     * TODO add sort and pageable
     */
    /*@Query("SELECT B FROM BOOKINGS AS B " +
            "LEFT JOIN ITEMS AS I ON I.ITEM.ID = B.ITEM.ID " +
            "WHERE I.ITEM.ID IN (:itemsIdList) AND B.STATUS = :status " +
            "ORDER BY B.START.TIME DESC")
    List<Booking> findByItemIdInAndStatus(@Param("itemsIdList") List<Long> itemsIdList,@Param("status") String status);*/

    /*@Query("SELECT B FROM Bookings AS B " +
            "WHERE B.ITEM.ID = :itemId")*/
    List<Booking> findByItemId(@Param("itemId") Long itemId);
}
