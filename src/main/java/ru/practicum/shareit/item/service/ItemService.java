package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getAllByUser(Long userId);

    Item getItem(Long itemId);

    Item create(Item item, Long userId);

    Item update(Long itemId, Long ownerId, ItemUpdateDto itemUpdateDto);

    void delete(Long id, Long ownerId);

    List<Item> getSearcherItems(String text);
}
