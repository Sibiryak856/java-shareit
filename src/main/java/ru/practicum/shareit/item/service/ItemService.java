package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    List<ItemResponseDto> getAllByUser(Long userId);

    ItemResponseDto getItem(Long itemId);

    ItemResponseDto create(ItemCreateDto itemCreateDto, Long userId);

    ItemResponseDto update(Long itemId, Long ownerId, ItemUpdateDto itemUpdateDto);

    void delete(Long id, Long ownerId);

    List<ItemResponseDto> getSearcherItems(String text);
}
