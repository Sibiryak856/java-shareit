package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    public ItemRepository itemRepository;
    private UserRepository userRepository;
    private ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }


    @Override
    public List<ItemResponseDto> getAllByUser(Long userId) {
        User user = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        return itemRepository.getAllByUser(userId).stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItem(Long itemId) {
        return itemMapper.toItemDto(
                itemRepository.getItem(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", itemId)))
        );
    }

    @Override
    public ItemResponseDto create(ItemCreateDto itemCreateDto, Long userId) {
        User user = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Item item = itemMapper.toItem(itemCreateDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemRepository.create(item, userId));
    }

    @Override
    public ItemResponseDto update(Long itemId, Long ownerId, ItemUpdateDto itemUpdateDto) {
        User owner = userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));
        Item updatingItem = itemRepository.getItem(itemId)
                .orElseThrow(() -> new NotFoundException("Updating item not found"));
        if (!updatingItem.getOwner().equals(owner)) {
            throw new NotAccessException("Only item's owner can update data");
        }
        itemRepository.update(itemMapper.update(itemUpdateDto, updatingItem), ownerId);
        return itemMapper.toItemDto(itemRepository.getItem(itemId).get());
    }

    @Override
    public void delete(Long id, Long ownerId) {
        User user = userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", ownerId)));
        Item deletingItem = itemRepository.getItem(id)
                .orElseThrow(() -> new NotFoundException("Deleting item not found"));
        if (!deletingItem.getOwner().getId().equals(ownerId)) {
            throw new NotAccessException("Only item's owner can delete data");
        }
        itemRepository.delete(id, ownerId);
    }

    @Override
    public List<ItemResponseDto> getSearcherItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByName(text.toLowerCase()).stream()
                .map(item-> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }
}
