package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
public class Booking {
    private Long id;
    @NotNull
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

}
