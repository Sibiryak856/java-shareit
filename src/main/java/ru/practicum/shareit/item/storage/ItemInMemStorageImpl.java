package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.*;

public class ItemInMemStorageImpl implements ItemStorage {

    private Long itemId = 0L;
    private Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> getItem(Long id) {
        return Optional.of(items.get(id));
    }

    @Override
    public Item create(Item item) {
        item.setId(++itemId);
        items.put(item.getId(), item);
        return null;
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }
}
