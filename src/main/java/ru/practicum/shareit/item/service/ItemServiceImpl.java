package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<ItemResponseDto> getAllByOwner(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        List<Item> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        List<Booking> bookingList = bookingRepository
                .findAllByItemIdInAndStatusNotLike(userItems
                        .stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()), BookingStatus.REJECTED);
        if (userItems.isEmpty()) {
            return Collections.emptyList();
        }
        for (Item item : userItems) {
            item.setLastBooking(getLastBooking(bookingList, item.getId(), now));
            item.setNextBooking(getNextBooking(bookingList, item.getId(), now));
        }
        return itemMapper.toListItemResponseDto(userItems);
    }

    @Override
    public ItemResponseDto getItem(Long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", itemId)));
        List<Booking> bookingList = bookingRepository.findAllByItemIdAndStatusNotLike(itemId, BookingStatus.REJECTED);
        if (item.getOwner().getId().equals(userId)) {
            if (!bookingList.isEmpty()) {
                item.setNextBooking(getNextBooking(bookingList, itemId, now));
                item.setLastBooking(getLastBooking(bookingList, itemId, now));
            }
        }
        item.setComments(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId));
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemResponseDto create(ItemCreateDto itemCreateDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Item item = itemMapper.toItem(itemCreateDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto update(Long itemId, Long ownerId, ItemUpdateDto itemUpdateDto) {
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
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", ownerId)));
        Item deletingItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Deleting item not found"));
        if (!deletingItem.getOwner().getId().equals(ownerId)) {
            throw new NotAccessException("Only item's owner can delete data");
        }
        itemRepository.deleteById(id);
    }

    // only available
    @Override
    public List<ItemResponseDto> getSearcherItems(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findAllByNameOrDescriptionIgnoreCaseContainingAndAvailableEquals(text, text, TRUE);
        List<Long> itemsId = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Comment> itemsComments = commentRepository.findAllByItemIdIn(itemsId);
        if (!itemsComments.isEmpty()) {
            for (Comment comment : itemsComments) {
                for (Item item : items) {
                    if (comment.getItem().getId().equals(item.getId())) {
                        item.addComment(comment);
                    }
                }
            }
        }
        return itemMapper.toListItemResponseDto(
                items);
    }

    @Override
    public CommentResponseDto create(CommentCreateDto commentDto, long userId, Long itemId) {
        commentDto.setCreated(LocalDateTime.now());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", itemId)));
        List<Booking> userBookings = bookingRepository
                .findPastBookingsByBookerAndItemAndStatus(userId, itemId, BookingStatus.APPROVED);
        if (userBookings.isEmpty()) {
            throw new IllegalArgumentException(String.format("User id=%d didn't book this item id=%d", userId, itemId));
        }
        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        return commentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    private Booking getLastBooking(List<Booking> bookingList, Long itemId, LocalDateTime now) {
        return bookingList.stream()
                .sorted(Comparator.comparing(Booking::getEndTime).reversed())
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getEndTime().isBefore(now))
                .findFirst()
                .orElse(null);
    }

    private Booking getNextBooking(List<Booking> bookingList, Long itemId, LocalDateTime now) {
        return bookingList.stream()
                .sorted(Comparator.comparing(Booking::getStartTime))
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStartTime().isAfter(now))
                .findFirst()
                .orElse(null);
    }
}
