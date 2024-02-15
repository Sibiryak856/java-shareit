package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    public ItemDao itemDao;
    private UserDao userDao;
    private BookingRepository bookingRepository;
    private ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao, BookingRepository bookingRepository, ItemMapper itemMapper) {
        this.itemDao = itemDao;
        this.userDao = userDao;
        this.bookingRepository = bookingRepository;
        this.itemMapper = itemMapper;
    }


    @Override
    public List<ItemResponseDto> getAllByUser(Long userId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        List<Item> userItems = itemDao.findByUserId(userId);
        if (userItems.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> itemsIdList = userItems.stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
        List<Booking> bookingsUserItems =
                bookingRepository.findByItemIdInAndStatus(itemsIdList, BookingStatus.APPROVED.name());
        List<Item> approvedItems = bookingsUserItems.stream()
                .map(Booking::getItem)
                .collect(Collectors.toList());

        for (Booking booking : bookingsUserItems) {

        }

        return itemDao.findByUserId(userId).stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItem(Long itemId) {
        return itemMapper.toItemDto(
                itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", itemId)))
        );
    }

    @Override
    public ItemResponseDto create(ItemCreateDto itemCreateDto, Long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Item item = itemMapper.toItem(itemCreateDto, user);
        return itemMapper.toItemDto(itemDao.save(item));
    }

    @Override
    public ItemResponseDto update(Long itemId, Long ownerId, ItemUpdateDto itemUpdateDto) {
        User owner = userDao.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));
        Item item = itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Updating item not found"));
        if (!item.getOwner().equals(owner)) {
            throw new NotAccessException("Only item's owner can update data");
        }
        Item updatedItem = itemDao.save(itemMapper.update(itemUpdateDto, item));
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public void delete(Long id, Long ownerId) {
        User user = userDao.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", ownerId)));
        Item deletingItem = itemDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Deleting item not found"));
        if (!deletingItem.getOwner().equals(user)) {
            throw new NotAccessException("Only item's owner can delete data");
        }
        itemDao.deleteById(id);
    }

    @Override
    public List<ItemResponseDto> getSearcherItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemDao
                .findAllByNameOrDescriptionContainingIgnoreCase(text.toLowerCase(), text.toLowerCase())
                .stream()
                .map(item-> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }
}
