package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface BookingMapper {

    BookingResponseDto toBookingResponseDto(Booking booking);

    Booking toBooking(BookingCreateDto bookingCreateDto, User booker, Item item, BookingStatus status);

    BookingResponseDto.UserDto map(User user);

    BookingResponseDto.ItemDto map(Item item);

    List<BookingResponseDto> toListBookingResponseDto(List<Booking> bookingList);

}
