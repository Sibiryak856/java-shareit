package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingState {

    ALL,

    CURRENT,

    FUTURE,

    PAST,

    REJECTED,

    WAITING;

    public static Optional<BookingState> from(String state) {
        for (BookingState value : BookingState.values()) {
            if (value.name().equals(state)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
