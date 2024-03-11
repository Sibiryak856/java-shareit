package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING,
        uses = {ItemMapper.class})
public interface RequestMapper {

    @Mapping(target = "id", ignore = true)
    ItemRequest toRequest(ItemRequestCreateDto requestCreateDto, User requestor, LocalDateTime created);


    ItemRequestDto toRequestDto(ItemRequest itemRequest);

    default List<ItemRequestDto> toRequestDtosList(List<ItemRequest> itemRequests, Map<Long, List<ItemDto>> requestItems) {
        if (itemRequests == null) {
            return null;
        } else {
            List<ItemRequestDto> list = new ArrayList(itemRequests.size());
            itemRequests.stream().map(this::toRequestDto).forEach(requestDto -> {
                requestDto.setItems(requestItems.getOrDefault(requestDto.getId(), Collections.emptyList()));
                list.add(requestDto);
            });
            return list;
        }
    }
}
