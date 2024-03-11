package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    public final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingDto create(BookingCreateDto bookingDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Item id=%d not found", bookingDto.getItemId())));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException(
                    String.format("This item id=%d is unavailable", bookingDto.getItemId()));
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotAccessException("Owner can't book his own item");
        }
        Booking booking = bookingMapper.toBooking(bookingDto, item, user, BookingStatus.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking id=%d not found", bookingId)));
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotAccessException("Only item's owner can approve booking");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IllegalArgumentException(
                    String.format("Status has already changed to %s", booking.getStatus()));
        }
        if (approved) booking.setStatus(BookingStatus.APPROVED);
        else booking.setStatus(BookingStatus.REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking id=%d not found", bookingId)));
        if (!(booking.getItem().getOwner().getId().equals(userId) ||
                booking.getBooker().getId().equals(userId))) {
            throw new NotAccessException("Only item's owner or booker can receive booking's data");
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUserQuery(long userId, BookingState state, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        List<Booking> requestedBooking;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                requestedBooking = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case CURRENT:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndStartTimeBeforeAndEndTimeAfter(userId, now, now, pageable);
                break;
            case FUTURE:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndStartTimeAfter(userId, now, pageable);
                break;
            case PAST:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndEndTimeBefore(userId, now, pageable);
                break;
            case REJECTED:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            case WAITING:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            default:
                requestedBooking = Collections.emptyList();
        }
        return bookingMapper.toListBookingDto(requestedBooking);
    }

    @Override
    public List<BookingDto> getAllByOwnerQuery(long userId, BookingState state, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        List<Booking> requestedBooking;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                requestedBooking = bookingRepository.findAllByItemOwnerId(userId, pageable);
                break;
            case CURRENT:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfter(userId, now, now, pageable);
                break;
            case FUTURE:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndStartTimeAfter(userId, now, pageable);
                break;
            case PAST:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndEndTimeBefore(userId, now, pageable);
                break;
            case REJECTED:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            case WAITING:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            default:
                requestedBooking = Collections.emptyList();
        }
        return bookingMapper.toListBookingDto(requestedBooking);
    }
}
