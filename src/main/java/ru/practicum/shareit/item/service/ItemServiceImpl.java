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

        List<Long> itemsId = userItems.stream()
                .map(ItemDto::getId)
                .collect(Collectors.toList());

        List<Booking> itemsBookings = bookingRepository
                .findAllByItemIdInAndStatusNotLike(itemsId, BookingStatus.REJECTED);
        Map<Long, List<Booking>> itemBookingsMap = new HashMap<>();
        for (Booking booking : itemsBookings) {
            Long id = booking.getItem().getId();
            if (itemBookingsMap.get(id) == null) {
                itemBookingsMap.put(id, new ArrayList<>());
            }
            List<Booking> bookings = itemBookingsMap.get(id);
            bookings.add(booking);
            itemBookingsMap.put(id, bookings);
        }

        List<Comment> itemsComments = commentRepository
                .findAllByItemIdIn(itemsId, Sort.by("created").descending());
        Map<Long, List<Comment>> itemsCommentsMap = new HashMap<>();
        for (Comment comment : itemsComments) {
            Long id = comment.getItem().getId();
            if (itemsCommentsMap.get(id) == null) {
                itemsCommentsMap.put(id, new ArrayList<>());
            }
            List<Comment> comments = itemsCommentsMap.get(id);
            comments.add(comment);
            itemsCommentsMap.put(id, comments);
        }

        for (ItemDto itemDto : userItems) {
            Long id = itemDto.getId();
            itemDto.setLastBooking(
                    itemMapper.map(getLastBooking(itemBookingsMap.get(id), now)));
            itemDto.setNextBooking(
                    itemMapper.map(getNextBooking(itemBookingsMap.get(id), now)));
            itemDto.setComments(commentMapper.toListCommentDto(itemsCommentsMap.get(itemDto.getId())));
        }
        return userItems;
    }

    @Override
    public ItemDto getItem(Long itemId, long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", itemId)));
        ItemDto itemDto = itemMapper.toItemDto(item);
        List<Booking> itemBookings = bookingRepository
                .findAllByItemIdAndStatusNotLike(
                        itemId, BookingStatus.REJECTED, Sort.by("startTime").ascending());
        if (item.getOwner().getId().equals(userId)) {
            itemDto.setNextBooking(
                    itemMapper.map(getNextBooking(itemBookings, now)));
            itemDto.setLastBooking(
                    itemMapper.map(getLastBooking(itemBookings, now)));
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
                itemRepository.findAllAvailableBySearch(text.toLowerCase(), text.toLowerCase(), TRUE));

        List<Long> itemsId = items.stream()
                .map(ItemDto::getId)
                .collect(Collectors.toList());

        List<Comment> itemsComments = commentRepository
                .findAllByItemIdIn(itemsId, Sort.by("created").descending());
        Map<Long, List<Comment>> itemsCommentsMap = new HashMap<>();
        for (Comment comment : itemsComments) {
            Long id = comment.getItem().getId();
            if (itemsCommentsMap.get(id) == null) {
                itemsCommentsMap.put(id, new ArrayList<>());
            }
            List<Comment> comments = itemsCommentsMap.get(id);
            comments.add(comment);
            itemsCommentsMap.put(id, comments);
        }

        for (ItemDto item : items) {
            item.setComments(commentMapper.toListCommentDto(itemsCommentsMap.get(item.getId())));
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

    private Booking getLastBooking(List<Booking> bookings, LocalDateTime now) {
        if (bookings == null) {
            return null;
        }
        Collections.reverse(bookings);
        return bookings.stream()
                .filter(booking -> booking.getStartTime().isBefore(now))
                .findFirst()
                .orElse(null);
    }

    private Booking getNextBooking(List<Booking> bookings, LocalDateTime now) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream()
                .filter(booking -> booking.getStartTime().isAfter(now))
                .findFirst()
                .orElse(null);
    }
}
