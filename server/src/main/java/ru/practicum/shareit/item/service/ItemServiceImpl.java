package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    public List<ItemDto> getAllByOwner(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        List<Item> userItems = itemRepository.findAllByOwnerId(userId, pageable);

        List<Long> itemIds = userItems.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> itemsBookings = bookingRepository
                .findAllByItemIdInAndStatusIs(itemIds, BookingStatus.APPROVED);
        Map<Long, List<Booking>> itemBookingsMap = itemsBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        List<Comment> itemsComments = commentRepository
                .findAllByItemIdIn(itemIds, Sort.by("created").descending());
        Map<Long, List<Comment>> itemsCommentsMap = itemsComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return itemMapper.toFullItemDtoList(userItems, itemBookingsMap, itemsCommentsMap);
    }

    @Override
    public ItemDto getItem(Long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", itemId)));
        ItemDto itemDto = itemMapper.toItemDto(item);
        List<Booking> itemBookings = bookingRepository
                .findAllByItemIdAndStatusIs(
                        itemId, BookingStatus.APPROVED, Sort.by("startTime").ascending());
        if (item.getOwner().getId().equals(userId)) {
            itemDto.setNextBooking(
                    itemMapper.map(getNextBooking(itemBookings)));
            itemDto.setLastBooking(
                    itemMapper.map(getLastBooking(itemBookings)));
        }
        itemDto.setComments(commentMapper
                .toListCommentDto(commentRepository.findAllByItemId(itemId, Sort.by("created").descending())));
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
        if (!userRepository.existsById(ownerId)) {
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
    public List<ItemDto> getSearcherItems(String text, Pageable pageable) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.findAllAvailableBySearch(
                        text.toLowerCase(),
                        text.toLowerCase(),
                        TRUE,
                        pageable);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Comment> itemsComments = commentRepository
                .findAllByItemIdIn(itemIds, Sort.by("created").descending());
        Map<Long, List<Comment>> itemsCommentsMap = itemsComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return itemMapper.toFullItemDtoList(items, Collections.EMPTY_MAP, itemsCommentsMap);
    }

    @Override
    public CommentDto create(CommentCreateDto commentDto, long userId, Long itemId) {
        LocalDateTime created = LocalDateTime.now();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", itemId)));
        List<Booking> userBookings = bookingRepository
                .findAllByBookerIdAndItemIdAndStatusIsAndEndTimeBefore(
                        userId, itemId, BookingStatus.APPROVED, created);
        if (userBookings.isEmpty()) {
            throw new IllegalArgumentException(String.format("User id=%d didn't book this item id=%d", userId, itemId));
        }
        Comment comment = commentMapper.toComment(commentDto, item, user, created);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    public static Booking getLastBooking(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        Collections.reverse(bookings);
        return bookings.stream()
                .filter(booking -> booking.getStartTime().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }

    public static Booking getNextBooking(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings.stream()
                .filter(booking -> booking.getStartTime().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }
}
