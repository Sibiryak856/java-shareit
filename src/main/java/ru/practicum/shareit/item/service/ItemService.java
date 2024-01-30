package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getAll();

    Item getItem(Long id);

    Item create(Item item, Long userId);

    Item update(Item item, Long ownerId);

    void delete(Long id);

}
