package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.Collection;

@Service
public class ItemServiceImpl implements ItemService {

    public ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public Collection<Item> getAll() {
        return itemStorage.getAll();
    }

    @Override
    public Item getItem(Long id) {
        return itemStorage.getItem(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%id not found", id)));
    }

    @Override
    public Item create(Item item) {
        return itemStorage.create(item);
    }

    @Override
    public Item update(Item item) {
        Item updatingItem = itemStorage.getItem(item.getId())
                .orElseThrow(() -> new NotFoundException("Updating item not found"));
        itemStorage.update(item);
        return itemStorage.getItem(item.getId()).get();
    }

    @Override
    public void delete(Long id) {
        itemStorage.delete(id);
    }
}
