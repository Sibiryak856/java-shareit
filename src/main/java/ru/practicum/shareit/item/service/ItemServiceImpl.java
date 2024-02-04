package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    public ItemStorage itemStorage;
    private UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }


    @Override
    public List<Item> getAllByUser(Long userId) {
        List<Item> items = itemStorage.getAll();
        List<Item> userItems = items.stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
        return userItems;
    }

    @Override
    public Item getItem(Long itemId) {
        return itemStorage.getItem(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", itemId)));
    }

    @Override
    public Item create(Item item, Long userId) {
        User user = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        item.setOwner(user);
        return itemStorage.create(item);
    }

    @Override
    public Item update(Long itemId, Long ownerId, ItemUpdateDto itemUpdateDto) {
        User owner = userStorage.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));;
        Item updatingItem = itemStorage.getItem(itemId)
                .orElseThrow(() -> new NotFoundException("Updating item not found"));
        if (!updatingItem.getOwner().equals(owner)) {
            throw new NotAccessException("Only item's owner can update data");
        }
        itemStorage.update(ItemMapper.INSTANCE.update(itemUpdateDto, updatingItem));
        return itemStorage.getItem(itemId).get();
    }

    @Override
    public void delete(Long id, Long ownerId) {
        User user = userStorage.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", ownerId)));
        Item updatingItem = itemStorage.getItem(id)
                .orElseThrow(() -> new NotFoundException("Deleting item not found"));
        if (!updatingItem.getOwner().getId().equals(ownerId)) {
            throw new NotAccessException("Only item's owner can update data");
        }
        itemStorage.delete(id);
    }

    @Override
    public List<Item> getSearcherItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        String lowCaseText = text.toLowerCase();
        return itemStorage.getAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowCaseText) ||
                        item.getDescription().toLowerCase().contains(lowCaseText))
                .collect(Collectors.toList());
    }
}
