package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOKINGS", schema = "PUBLIC")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOOKING_ID")
    Long bookingId;

    @Column(name = "START_TIME")
    LocalDateTime startTime;

    @Column(name = "END_TIME")
    LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    Item item;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    User booker;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    BookingStatus status;
}
