package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {


    List<ItemResponseDto> getAllByOwner(Long userId);

    ItemResponseDto getItem(Long itemId, long userId);

    ItemResponseDto create(ItemCreateDto itemCreateDto, Long userId);

    ItemResponseDto update(Long itemId, Long ownerId, ItemUpdateDto itemUpdateDto);

    void delete(Long id, Long ownerId);

    List<ItemResponseDto> getSearcherItems(String text);

    CommentResponseDto create(CommentCreateDto commentDto, long userId, Long itemId);
}
