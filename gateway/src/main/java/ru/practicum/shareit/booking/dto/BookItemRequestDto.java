package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validation.StartBeforeEnd;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEnd
public class BookItemRequestDto {
	private Long itemId;
	private LocalDateTime start;
	private LocalDateTime end;
}
