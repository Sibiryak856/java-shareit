package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface RequestMapper {

    @Mapping(target = "id", ignore = true)
    ItemRequest toRequest(ItemRequestCreateDto requestCreateDto, User requestor, LocalDateTime created);


    ItemRequestDto toRequestDto(ItemRequest itemRequest);

    List<ItemRequestDto> toRequestDtosList(List<ItemRequest> itemRequests);
}
