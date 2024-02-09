package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemInMemRepository implements ItemRepository {

    private Long itemId = 0L;
    private Map<Long, Item> items = new HashMap<>();
    private Map<Long, List<Item>> userItems = new HashMap<>();

    @Override
    public List<Item> getAllByUser(Long userId) {
        return userItems.get(userId);
    }

    @Override
    public Optional<Item> getItem(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item create(Item item, Long userId) {
        item.setId(++itemId);
        items.put(item.getId(), item);
        List<Item> userItemList = userItems.get(userId);
        if (userItemList == null) {
            userItemList = new ArrayList<>();
        }
        userItemList.add(item);
        userItems.put(item.getOwner().getId(), userItemList);
        return item;
    }

    @Override
    public void update(Item item, Long userId) {
        List<Item> userItemList = userItems.get(userId);
        userItemList.set(userItemList.indexOf(items.get(item.getId())), item);
        userItems.put(userId, userItemList);
        items.put(item.getId(), item);
    }

    @Override
    public void delete(Long id, Long userId) {
        List<Item> userItemList = userItems.get(userId);
        userItemList.remove(items.get(id));
        items.remove(id);
    }

    @Override
    public List<Item> findByName(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByUser(Long id) {
        List<Item> itemList = userItems.remove(id);
        if (itemList != null) {
            itemList.forEach(item -> items.remove(item.getId()));
        }
    }
}
