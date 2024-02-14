package ru.practicum.shareit.booking;

public enum BookingState {

    ALL,

    CURRENT,

    FUTURE,

    PAST,

    REJECTED,

    WAITING;

    public static BookingState from(String state) {
        for (BookingState value : BookingState.values()) {
            if (value.name().equals(state)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown state: " + state);
    }
}
