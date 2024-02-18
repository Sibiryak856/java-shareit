package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING,
        uses = CommentMapper.class)
public interface ItemMapper {

    ItemResponseDto toItemDto(Item item);

    Item toItem(ItemCreateDto itemCreateDto);

    @Mapping(source = "booker.id", target = "bookerId")
    ItemResponseDto.BookingDto map(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    Item update(ItemUpdateDto itemUpdateDto, @MappingTarget Item item);

    List<ItemResponseDto> toListItemResponseDto(List<Item> items);
}
