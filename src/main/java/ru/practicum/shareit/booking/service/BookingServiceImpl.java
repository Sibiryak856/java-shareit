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
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserDao;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static ru.practicum.shareit.booking.BookingState.REJECTED;
import static ru.practicum.shareit.booking.BookingState.WAITING;

@Service
public class BookingServiceImpl implements BookingService {

    public final BookingRepository bookingRepository;
    private final UserDao userDao;
    private final ItemDao itemDao;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserDao userDao, ItemDao itemDao, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userDao = userDao;
        this.itemDao = itemDao;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingResponseDto create(BookingCreateDto bookingDto, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Item item = itemDao.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Item id=%d not found", bookingDto.getItemId())));
        Booking booking = bookingMapper.toBooking(bookingDto, user, item, BookingStatus.WAITING);
        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto update(Long bookingId, long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking id=%d not found", bookingId)));
        if (!userDao.existsById(userId)) {
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
        item.setAvailable(FALSE);
        itemDao.save(item);
        bookingRepository.save(booking);
        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, long userId) {
        if (!userDao.existsById(userId)) {
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
        if (!userDao.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        List<Booking> requestedBooking;
        switch (bookingState) {
            case ALL:
                requestedBooking = bookingRepository.findByUserIdOrderByStartTimeDesc(userId);
                break;
            case CURRENT:
                requestedBooking = bookingRepository.findCurrentBookingByUser(userId);
                break;
            case FUTURE:
                requestedBooking = bookingRepository
                        .findByUserIdAndStartTimeAfterOrderByStartTimeDesc(userId, LocalDateTime.now());
                break;
            case PAST:
                requestedBooking = bookingRepository
                        .findByUserIdAndEndTimeBeforeOrderByStartTimeDesc(userId, LocalDateTime.now());
                break;
            case REJECTED:
                requestedBooking = bookingRepository.findBookingByUserIdAndStatusOrderByStartTimeDesc(userId, REJECTED.name());
                break;
            case WAITING:
                requestedBooking = bookingRepository.findBookingByUserIdAndStatusOrderByStartTimeDesc(userId, WAITING.name());;
                break;
            default:
                requestedBooking = Collections.emptyList();
        }
        return bookingMapper.toListBookingResponseDto(requestedBooking);
    }

    @Override
    public List<BookingResponseDto> getAllByOwnerQuery(long userId, String state) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        List<Booking> requestedBooking;
        switch (bookingState) {
            case ALL:
                requestedBooking = bookingRepository.findAllBookingByItemOwner(userId);
                break;
            case CURRENT:
                requestedBooking = bookingRepository.findCurrentBookingByOwner(userId);
                break;
            case FUTURE:
                requestedBooking = bookingRepository.findFutureBookingByOwner(userId);
                break;
            case PAST:
                requestedBooking = bookingRepository.findPastBookingByOwner(userId);
                break;
            case REJECTED:
                requestedBooking = bookingRepository.findBookingByOwnerAndStatus(userId, REJECTED.name());
                break;
            case WAITING:
                requestedBooking = bookingRepository.findBookingByOwnerAndStatus(userId, WAITING.name());;
                break;
            default:
                requestedBooking = Collections.emptyList();
        }
        return bookingMapper.toListBookingResponseDto(requestedBooking);
    }
}
