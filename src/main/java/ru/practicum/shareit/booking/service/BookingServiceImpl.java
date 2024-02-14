package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
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

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.BookingState.*;

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
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto update(Long bookingId, long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking id=%d not found", bookingId)));
        if (!userDao.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotAccessException("Only item's owner can approve booking");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
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
    public List<BookingDto> getByOwnerQuery(long userId, String state) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        switch (from(state)) {
            case ALL:
                return null;
            case CURRENT:
                return null;
            case FUTURE:
                return null;
            case PAST:
                return null;
            case REJECTED:
                return null;
            case WAITING:
                return null;
            default:
                return new ArrayList<>();

        }
    }


}
