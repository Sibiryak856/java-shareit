package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User booker, BookingStatus status);

    BookingDto.UserDto map(User user);

    BookingDto.ItemDto map(Item item);

    List<BookingDto> toListBookingDto(List<Booking> bookingList);

}
