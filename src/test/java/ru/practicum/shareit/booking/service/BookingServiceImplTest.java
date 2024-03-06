package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingState.*;
import static ru.practicum.shareit.booking.model.BookingStatus.*;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingCreateDto bookingCreateDto;
    private BookingDto bookingDto;
    private Booking booking;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(1).withNano(0);
        LocalDateTime end = start.plusMinutes(10);
        booker = User.builder()
                .id(1L)
                .name("name")
                .email("name@email.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(TRUE)
                .requestId(null)
                .owner(User.builder().id(10L).build())
                .build();
        bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .startTime(start)
                .endTime(end)
                .build();
        bookingDto = BookingDto.builder()
                .id(1L)
                .startTime(start)
                .endTime(end)
                .status(WAITING)
                .booker(BookingDto.UserDto.builder()
                        .id(1L)
                        .build())
                .item(BookingDto.ItemDto.builder()
                        .id(1L).name("name")
                        .build())
                .build();
        booking = Booking.builder()
                .id(1L)
                .startTime(start)
                .endTime(end)
                .item(item)
                .booker(booker)
                .status(WAITING)
                .build();
    }

    @AfterEach
    void clean() {
        item = null;
        bookingCreateDto = null;
        bookingDto = null;
        booking = null;
    }

    @Test
    void create_whenBookingIsValid_thenReturnBookingDto() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDto result = bookingService.create(bookingCreateDto, booker.getId());

        assertThat(result).isEqualTo(bookingDto);
    }

    @Test
    void create_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 100L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingCreateDto, userId));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenItemNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingCreateDto, booker.getId()));

        assertThat(e.getMessage()).isEqualTo(String.format("Item id=%d not found", bookingCreateDto.getItemId()));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenItemNotAvailable_thenIllegalArgumentExceptionThrown() {
        item.setAvailable(FALSE);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> bookingService.create(bookingCreateDto, booker.getId()));

        assertThat(e.getMessage()).isEqualTo(String.format("This item id=%d is unavailable", bookingCreateDto.getItemId()));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenBookerIsOwner_thenNotAccessExceptionThrown() {
        item.setOwner(User.builder()
                .id(booker.getId())
                .build());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        NotAccessException e = assertThrows(NotAccessException.class,
                () -> bookingService.create(bookingCreateDto, booker.getId()));

        assertThat(e.getMessage()).isEqualTo("Owner can't book his own item");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void update_whenArgsIsValid_thenReturnApprovedBookingDto() {
        bookingDto.setStatus(APPROVED);
        item.setOwner(booker);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.save((any(Booking.class))))
                .thenReturn(booking);

        BookingDto result = bookingService.update(booking.getId(), booker.getId(), TRUE);

        assertThat(result).isEqualTo(bookingDto);
    }

    @Test
    void update_whenArgsIsValid_thenReturnRejectedBookingDto() {
        bookingDto.setStatus(REJECTED);
        item.setOwner(booker);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.save((any(Booking.class))))
                .thenReturn(booking);

        BookingDto result = bookingService.update(booking.getId(), booker.getId(), FALSE);

        assertThat(result).isEqualTo(bookingDto);
    }

    @Test
    void update_whenBookingNotFound_thenNotFoundExceptionThrown() {
        Long bookingId = 100L;
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.update(bookingId, booker.getId(), TRUE));

        assertThat(e.getMessage()).isEqualTo(String.format("Booking id=%d not found", bookingId));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void update_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 100L;
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.existsById(anyLong()))
                .thenReturn(FALSE);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.update(booking.getId(), userId, TRUE));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void update_whenUserIsNotOwner_thenNotAccessExceptionThrown() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);

        NotAccessException e = assertThrows(NotAccessException.class,
                () -> bookingService.update(booking.getId(), booker.getId(), TRUE));

        assertThat(e.getMessage()).isEqualTo("Only item's owner can approve booking");

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void update_whenStatusIsNotWaiting_thenIllegalArgumentExceptionThrown() {
        booking.setStatus(APPROVED);
        item.setOwner(booker);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> bookingService.update(booking.getId(), booker.getId(), TRUE));

        assertThat(e.getMessage()).isEqualTo(String.format("Status has already changed to %s", booking.getStatus()));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBooking_whenArgsIsValid_thenReturnBookingDto() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(booking.getId(), booker.getId());

        assertThat(result).isEqualTo(bookingDto);
    }

    @Test
    void getBooking_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 100L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(FALSE);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(booking.getId(), userId));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));
    }

    @Test
    void getBooking_whenBookingNotFound_thenNotFoundExceptionThrown() {
        Long bookingId = 100L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(bookingId, booker.getId()));

        assertThat(e.getMessage()).isEqualTo(String.format("Booking id=%d not found", bookingId));
    }

    @Test
    void getBooking_whenUserIsNotOwnerOrBooker_thenNotAccessExceptionThrown() {
        Long userId = 100L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        NotAccessException e = assertThrows(NotAccessException.class,
                () -> bookingService.getBooking(booking.getId(), userId));

        assertThat(e.getMessage()).isEqualTo("Only item's owner or booker can receive booking's data");
    }

    @Test
    void getAllByUserQuery_whenStateIsAll_thenReturnAllBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByUserQuery(userId, ALL, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByBookerId(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllByUserQuery_whenStateIsCurrent_thenReturnCurrentBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByBookerIdAndStartTimeBeforeAndEndTimeAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByUserQuery(userId, CURRENT, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByBookerIdAndStartTimeBeforeAndEndTimeAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByUserQuery_whenStateIsFuture_thenReturnFutureBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByBookerIdAndStartTimeAfter(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByUserQuery(userId, FUTURE, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByBookerIdAndStartTimeAfter(
                anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByUserQuery_whenStateIsPast_thenReturnPastBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByBookerIdAndEndTimeBefore(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByUserQuery(userId, PAST, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByBookerIdAndEndTimeBefore(
                anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByUserQuery_whenStateIsRejected_thenReturnRejectedBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByBookerIdAndStatus(
                anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByUserQuery(userId, BookingState.REJECTED, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByBookerIdAndStatus(
                anyLong(), any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getAllByUserQuery_whenStateIsWaiting_thenReturnWaitingBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByBookerIdAndStatus(
                anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByUserQuery(userId, BookingState.WAITING, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByBookerIdAndStatus(
                anyLong(), any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getAllByUserQuery_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(FALSE);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getAllByUserQuery(userId, ALL, from, size));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));

        verify(bookingRepository, never()).findAllByBookerId(
                anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerIdAndStartTimeBeforeAndEndTimeAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerIdAndStartTimeAfter(
                anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerIdAndEndTimeBefore(
                anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByBookerIdAndStatus(
                anyLong(), any(BookingStatus.class), any(Pageable.class));
    }

@Test
void getAllByOwnerQuery_whenStateIsAll_thenReturnAllBookings() {
    Long userId = 2L;
    int from = 5;
    int size = 10;
    when(userRepository.existsById(anyLong()))
            .thenReturn(TRUE);
    when(bookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class)))
            .thenReturn(List.of(booking));

    List<BookingDto> result = bookingService.getAllByOwnerQuery(userId, ALL, from, size);

    assertThat(result).isEqualTo(List.of(bookingDto));

    verify(bookingRepository).findAllByItemOwnerId(anyLong(), any(Pageable.class));
}

    @Test
    void getAllByOwnerQuery_whenStateIsCurrent_thenReturnCurrentBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerQuery(userId, CURRENT, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerQuery_whenStateIsFuture_thenReturnFutureBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByItemOwnerIdAndStartTimeAfter(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerQuery(userId, FUTURE, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByItemOwnerIdAndStartTimeAfter(
                anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerQuery_whenStateIsPast_thenReturnPastBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByItemOwnerIdAndEndTimeBefore(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerQuery(userId, PAST, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByItemOwnerIdAndEndTimeBefore(
                anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerQuery_whenStateIsRejected_thenReturnRejectedBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByItemOwnerIdAndStatus(
                anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerQuery(userId, BookingState.REJECTED, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByItemOwnerIdAndStatus(
                anyLong(), any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerQuery_whenStateIsWaiting_thenReturnWaitingBookings() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(bookingRepository.findAllByItemOwnerIdAndStatus(
                anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllByOwnerQuery(userId, BookingState.WAITING, from, size);

        assertThat(result).isEqualTo(List.of(bookingDto));

        verify(bookingRepository).findAllByItemOwnerIdAndStatus(
                anyLong(), any(BookingStatus.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerQuery_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 2L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong()))
                .thenReturn(FALSE);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getAllByOwnerQuery(userId, ALL, from, size));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));

        verify(bookingRepository, never()).findAllByItemOwnerId(
                anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStartTimeAfter(
                anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerIdAndEndTimeBefore(
                anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).findAllByItemOwnerIdAndStatus(
                anyLong(), any(BookingStatus.class), any(Pageable.class));
    }
}