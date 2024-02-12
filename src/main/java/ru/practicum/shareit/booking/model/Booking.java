package ru.practicum.shareit.booking.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

/**
 * TODO Sprint add-bookings.
 */

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Booking {

    @Id
    Long bookingId;

    Instant startBooking;

    Instant endBooking;

    Item item;

    User booker;

    BookingStatus status;
}
