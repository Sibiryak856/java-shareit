package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING,
        uses = {CommentMapper.class, UserMapper.class})
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "itemCreateDto.name", target = "name")
    Item toItem(ItemCreateDto itemCreateDto, User owner);

    @Mapping(source = "booker.id", target = "bookerId")
    ItemDto.BookingDto map(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    Item update(ItemUpdateDto itemUpdateDto, @MappingTarget Item item);

    List<ItemDto> toListItemDto(List<Item> items);
}
