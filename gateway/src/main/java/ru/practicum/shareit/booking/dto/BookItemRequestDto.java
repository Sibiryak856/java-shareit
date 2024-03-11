package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validation.StartBeforeEnd;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEnd
public class BookItemRequestDto {
	private Long itemId;
	private LocalDateTime start;
	private LocalDateTime end;
}
