package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {


    List<ItemDto> getAllByOwner(Long userId, Pageable pageable);

    ItemDto getItem(Long itemId, long userId);

    ItemDto create(ItemCreateDto itemCreateDto, Long userId);

    ItemDto update(Long itemId, Long ownerId, ItemUpdateDto itemUpdateDto);

    void delete(Long id, Long ownerId);

    List<ItemDto> getSearcherItems(String text, Pageable pageable);

    CommentDto create(CommentCreateDto commentDto, long userId, Long itemId);
}
