package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> getAllByUser(Long userId);

    Optional<Item> getItem(Long id);

    Item create(Item item, Long userId);

    void update(Item item, Long userId);

    void delete(Long id, Long userId);

    List<Item> findByName(String text);

    void deleteAllByUser(Long id);
}
