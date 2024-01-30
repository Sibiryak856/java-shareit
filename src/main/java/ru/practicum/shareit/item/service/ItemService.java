package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Collection<Item> getAll();

    Item getItem(Long id);

    Item create(Item item);

    Item update(Item item);

    void delete(Long id);

}
