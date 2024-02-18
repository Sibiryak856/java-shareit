package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.booking.validation.StartBeforeEnd;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@StartBeforeEnd
public class BookingCreateDto {

    @NotNull
    Long itemId;

    @JsonProperty("start")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startTime;

    @JsonProperty("end")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endTime;
}
