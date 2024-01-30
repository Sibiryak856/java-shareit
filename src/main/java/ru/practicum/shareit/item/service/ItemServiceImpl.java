package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    public ItemStorage itemStorage;

    /*@Autowired
    public ItemServiceImpl(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }*/

    @Override
    public List<Item> getAll() {
        return itemStorage.getAll();
    }

    @Override
    public Item getItem(Long id) {
        return itemStorage.getItem(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%id not found", id)));
    }

    @Override
    public Item create(Item item, Long userId) {
        return itemStorage.create(item);
    }

    @Override
    public Item update(Item item, Long ownerId) {
        Item updatingItem = itemStorage.getItem(item.getId())
                .orElseThrow(() -> new NotFoundException("Updating item not found"));
        if (!updatingItem.getOwner().getId().equals(ownerId)) {
            throw new NotAccessException("Only item's owner can update data");
        }
        itemStorage.update(item);
        return itemStorage.getItem(item.getId()).get();
    }

    @Override
    public void delete(Long id) {
        itemStorage.delete(id);
    }
}
