package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static ru.practicum.shareit.item.service.ItemServiceImpl.getLastBooking;
import static ru.practicum.shareit.item.service.ItemServiceImpl.getNextBooking;

@Component
@Mapper(componentModel = SPRING,
        uses = {CommentMapper.class, UserMapper.class})
public interface ItemMapper {

    CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
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

    default List<ItemDto> toFullItemDtoList(
            List<Item> userItems,
            Map<Long, List<Booking>> itemBookingsMap,
            Map<Long, List<Comment>> itemsCommentsMap) {
        if (userItems == null) {
            return null;
        } else {
            List<ItemDto> list = new ArrayList<>(userItems.size());
            userItems.stream().map(this::toItemDto).forEach(itemDto -> {
                long id = itemDto.getId();
                itemDto.setComments(
                        commentMapper.toListCommentDto(
                                itemsCommentsMap.getOrDefault(id, Collections.emptyList())));
                itemDto.setLastBooking(
                        map(getLastBooking(itemBookingsMap.getOrDefault(id, Collections.emptyList()))));
                itemDto.setNextBooking(
                        map(getNextBooking(itemBookingsMap.getOrDefault(id, Collections.emptyList()))));
                list.add(itemDto);
            });
            return list;
        }
    }
}
