package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    List<Item> getAll();

    Optional<Item> getItem(Long id);

    Item create(Item item);

    void update(Item item);

    void delete(Long id);
}
