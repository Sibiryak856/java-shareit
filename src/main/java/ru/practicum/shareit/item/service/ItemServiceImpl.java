package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Boolean.TRUE;

@Service
public class ItemServiceImpl implements ItemService {

    public ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemMapper itemMapper;
    private CommentMapper commentMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository, ItemMapper itemMapper, CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
    }

    @Override
    public List<ItemDto> getAllByOwner(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        Sort sort = Sort.by("id").ascending();
        List<ItemDto> userItems = itemMapper.toListItemDto(
                itemRepository.findAllByOwnerId(userId, sort));
        if (userItems.isEmpty()) {
            return Collections.emptyList();
        }

        for (ItemDto itemDto : userItems) {
            itemDto.setLastBooking(
                    itemMapper.map(getLastBooking(itemDto.getId(), now)));
            itemDto.setNextBooking(
                    itemMapper.map(getNextBooking(itemDto.getId(), now)));
            itemDto.setComments(commentMapper.toListCommentDto(
                    commentRepository.findAllByItemId(itemDto.getId(), sort)));
        }
        return userItems;
    }

    @Override
    public ItemDto getItem(Long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", itemId)));
        ItemDto itemDto = itemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            itemDto.setNextBooking(
                    itemMapper.map(getNextBooking(itemId, now)));
            itemDto.setLastBooking(
                    itemMapper.map(getLastBooking(itemId, now)));
        }
        Sort sort = Sort.by("created").descending();
        itemDto.setComments(commentMapper
                .toListCommentDto(commentRepository.findAllByItemId(itemId, sort)));
        return itemDto;
    }

    @Override
    public ItemDto create(ItemCreateDto itemCreateDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Item item = itemMapper.toItem(itemCreateDto, user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long itemId, Long ownerId, ItemUpdateDto itemUpdateDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Updating item not found"));
        if (!item.getOwner().equals(owner)) {
            throw new NotAccessException("Only item's owner can update data");
        }
        Item updatedItem = itemRepository.save(itemMapper.update(itemUpdateDto, item));
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public void delete(Long id, Long ownerId) {
        if (userRepository.existsById(ownerId)) {
            throw new NotFoundException(String.format("User id=%d not found", ownerId));
        }
        Item deletingItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Deleting item not found"));
        if (!deletingItem.getOwner().getId().equals(ownerId)) {
            throw new NotAccessException("Only item's owner can delete data");
        }
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> getSearcherItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> items = itemMapper.toListItemDto(
                itemRepository.findAllByNameOrDescriptionIgnoreCaseContainingAndAvailableEquals(text, text, TRUE));
        Sort sort = Sort.by("created").descending();
        for (ItemDto item : items) {
            item.setComments(commentMapper.toListCommentDto(
                    commentRepository.findAllByItemId(item.getId(), sort)));
        }
        return items;
    }

    @Override
    public CommentDto create(CommentCreateDto commentDto, long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", itemId)));
        List<Booking> userBookings = bookingRepository
                .findAllByBookerIdAndItemIdAndStatusIsAndEndTimeBefore(
                        userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (userBookings.isEmpty()) {
            throw new IllegalArgumentException(String.format("User id=%d didn't book this item id=%d", userId, itemId));
        }
        Comment comment = commentMapper.toComment(commentDto, item, user, LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Booking getLastBooking(Long itemId, LocalDateTime now) {
        return bookingRepository
                .findFirstByItemIdAndStartTimeBeforeAndStatusNotLike(
                        itemId,
                        now,
                        BookingStatus.REJECTED,
                        Sort.by("endTime").descending())
                .orElse(null);
    }

    private Booking getNextBooking(Long itemId, LocalDateTime now) {
        return bookingRepository
                .findFirstByItemIdAndStartTimeAfterAndStatusNotLike(
                        itemId,
                        now,
                        BookingStatus.REJECTED,
                        Sort.by("startTime").ascending())
                .orElse(null);
    }
}
