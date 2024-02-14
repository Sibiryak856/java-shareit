package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingCreateDto {

    @NotNull
    Long itemId;

    @NotNull
    @FutureOrPresent
    LocalDateTime startTime;

    @NotNull
    @Future
    LocalDateTime endTime;

}
