package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
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
    public BookingResponseDto create(BookingCreateDto bookingDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Item id=%d not found", bookingDto.getItemId())));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException(
                    String.format("This item id=%d is unavailable", bookingDto.getItemId()));
        }
        if (item.getOwner().equals(user)) {
            throw new NotAccessException("Owner can't book his own item");
        }
        Booking booking = bookingMapper.toBooking(bookingDto, BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto update(Long bookingId, long userId, Boolean approved) {
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
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        //item.setAvailable(FALSE);
        //itemRepository.save(item);
        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking id=%d not found", bookingId)));
        if (!(booking.getItem().getOwner().getId().equals(userId) ||
                booking.getBooker().getId().equals(userId))) {
            throw new NotAccessException("Only item's owner or booker can receive booking's data");
        }
        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllByUserQuery(long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        List<Booking> requestedBooking;
        switch (bookingState) {
            case ALL:
                requestedBooking = bookingRepository.findAllByBookerIdOrderByStartTimeDesc(userId);
                break;
            case CURRENT:
                requestedBooking = bookingRepository
                        .findCurrentBookingsByBooker(userId);
                break;
            case FUTURE:
                requestedBooking = bookingRepository
                        .findFutureBookingsByBooker(userId);
                break;
            case PAST:
                requestedBooking = bookingRepository
                        .findPastBookingsByBooker(userId);
                break;
            case REJECTED:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartTimeDesc(userId, BookingStatus.REJECTED);
                break;
            case WAITING:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartTimeDesc(userId, BookingStatus.WAITING);
                break;
            default:
                requestedBooking = Collections.emptyList();
        }
        return bookingMapper.toListBookingResponseDto(requestedBooking);
    }

    @Override
    public List<BookingResponseDto> getAllByOwnerQuery(long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        List<Booking> requestedBooking;
        switch (bookingState) {
            case ALL:
                requestedBooking = bookingRepository.findAllBookingsByOwner(userId);
                break;
            case CURRENT:
                requestedBooking = bookingRepository
                        .findCurrentBookingsByOwner(userId);
                break;
            case FUTURE:
                requestedBooking = bookingRepository
                        .findFutureBookingsByOwner(userId);
                break;
            case PAST:
                requestedBooking = bookingRepository
                        .findPastBookingsByOwner(userId);
                break;
            case REJECTED:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(userId, BookingStatus.REJECTED);
                break;
            case WAITING:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByStartTimeDesc(userId, BookingStatus.WAITING);
                break;
            default:
                requestedBooking = Collections.emptyList();
        }
        return bookingMapper.toListBookingResponseDto(requestedBooking);
    }
}
