package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private List<Item> items;

    @BeforeEach
    public void setUp() {
        owner = User.builder()
                .id(1L)
                .name("name")
                .email("name@email.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(TRUE)
                .owner(owner)
                .requestId(null)
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(TRUE)
                .owner(owner)
                .requestId(null)
                .comments(Collections.emptyList())
                .build();
        itemCreateDto = ItemCreateDto.builder()
                .name("name")
                .description("description")
                .available(TRUE)
                .requestId(null)
                .build();
        itemUpdateDto = ItemUpdateDto.builder()
                .id(1L)
                .name("newName")
                .build();
        items = new ArrayList<>();
    }

    @AfterEach
    public void clean() {
        owner = null;
        item = null;
        items = null;
    }


    @Test
    void getAllByOwner_whenOwnerFound_thenReturnItems() {
        int from = 5;
        int size = 10;
        items.add(item);
        List<ItemDto> itemDtoList = List.of(itemDto);
        when(userRepository.existsById(anyLong())).thenReturn(TRUE);
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(items);

        List<ItemDto> actualItemDtoList = itemService.getAllByOwner(
                owner.getId(),
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id")));

        assertThat(actualItemDtoList).isNotNull();
        assertThat(actualItemDtoList).isEqualTo(itemDtoList);

        verify(itemRepository).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllByOwner_whenOwnerNotFound_thenNotFoundExceptionThrown() {
        Long userId = 100L;
        int from = 5;
        int size = 10;
        when(userRepository.existsById(anyLong())).thenReturn(FALSE);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getAllByOwner(
                        userId,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"))));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));

        verify(itemRepository, never()).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void getItem_whenItemFound_thenReturnItemDto() {
        itemDto.setComments(null);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(null);
        ItemDto actualItemDto = itemService.getItem(item.getId(), owner.getId());

        assertThat(actualItemDto).isNotNull();
        assertThat(actualItemDto).isEqualTo(itemDto);

        verify(itemRepository).findById(anyLong());
    }

    @Test
    void getItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getItem(item.getId(), owner.getId()));

        assertThat(e.getMessage())
                .isEqualTo(String.format("Item id=%d not found", item.getId()));

        verify(itemRepository).findById(anyLong());
    }

    @Test
    void getItem_whenItemFound_thenReturnItemDtoWithLastBookingAndComments() {
        Booking lastBooking = Booking.builder()
                .id(1L)
                .startTime(LocalDateTime.now().minusMinutes(1))
                .endTime(LocalDateTime.now().plusMinutes(10))
                .item(item)
                .booker(User.builder()
                        .id(2L)
                        .name("name")
                        .email("user2@email.com")
                        .build())
                .status(BookingStatus.APPROVED)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(new User())
                .created(LocalDateTime.now().minusHours(2))
                .build();
        itemDto.setLastBooking(itemMapper.map(lastBooking));
        itemDto.setComments(List.of(commentMapper.toCommentDto(comment)));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndStatusIs(anyLong(), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(List.of(lastBooking));
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(comment));

        ItemDto actualItemDto = itemService.getItem(item.getId(), owner.getId());

        assertThat(actualItemDto).isNotNull();
        assertThat(actualItemDto).isEqualTo(itemDto);

        verify(itemRepository).findById(anyLong());
    }

    @Test
    void create_whenUserFound_thenReturnSavedItem() {
        itemDto.setComments(null);
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto savedItemDto = itemService.create(itemCreateDto, owner.getId());

        assertThat(savedItemDto).isEqualTo(itemDto);

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void create_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.create(itemCreateDto, owner.getId()));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", owner.getId()));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenUserIsOwnerAndItemFound_thenReturnUpdatedItemDto() {
        itemDto.setComments(null);
        itemDto.setName(itemUpdateDto.getName());
        itemDto.setName(itemUpdateDto.getName());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto updatedItemDto =
                itemService.update(itemUpdateDto.getId(), owner.getId(), itemUpdateDto);

        assertThat(updatedItemDto).isEqualTo(itemDto);

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long itemId = 1L;
        Long userId = 10L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.update(itemId, userId, itemUpdateDto));

        assertThat(e.getMessage()).isEqualTo("Owner not found");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenUserIsNotOwner_thenNotAccessExceptionThrown() {
        User notOwner = User.builder()
                .id(3L)
                .name("name3")
                .email("name3@email.com")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(notOwner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        NotAccessException e = assertThrows(NotAccessException.class,
                () -> itemService.update(item.getId(), notOwner.getId(), itemUpdateDto));

        assertThat(e.getMessage()).isEqualTo("Only item's owner can update data");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenItemNotFound_thenNotFoundExceptionThrown() {
        Long itemId = 10L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.update(itemId, owner.getId(), itemUpdateDto));

        assertThat(e.getMessage()).isEqualTo("Updating item not found");

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void delete_whenUserIsOwnerAndFound_thenSuccess() {
        willDoNothing().given(itemRepository).deleteById(anyLong());
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        itemService.delete(item.getId(), owner.getId());

        verify(itemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void delete_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 10L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(FALSE);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.delete(item.getId(), userId));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));

        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_whenUserIsNotOwner_thenNotAccessExceptionThrown() {
        User notOwner = User.builder()
                .id(3L)
                .name("name3")
                .email("name3@email.com")
                .build();
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        NotAccessException e = assertThrows(NotAccessException.class,
                () -> itemService.delete(item.getId(), notOwner.getId()));

        assertThat(e.getMessage()).isEqualTo("Only item's owner can delete data");

        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_whenItemNotFound_thenNotFoundExceptionThrown() {
        Long itemId = 10L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.delete(itemId, owner.getId()));

        assertThat(e.getMessage()).isEqualTo("Deleting item not found");

        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void getSearcherItems_whenTextIsEmpty_thenReturnEmptyList() {
        String text = "";
        int from = 5;
        int size = 10;
        List<ItemDto> itemDtoList = itemService.getSearcherItems(
                text,
                PageRequest.of(from / size, size));

        assertThat(itemDtoList).isNotNull();
        assertThat(itemDtoList.size()).isEqualTo(0);
    }

    @Test
    void getSearcherItems_whenRequestIsValid_thenReturnItemsList() {
        String text = "name";
        int from = 5;
        int size = 10;
        items.add(item);
        itemDto.setComments(Collections.emptyList());
        when(itemRepository.findAllAvailableBySearch(
                anyString(), anyString(), any(Boolean.class), any(Pageable.class)))
                .thenReturn(items);
        when(commentRepository.findAllByItemIdIn(anyList(), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        List<ItemDto> itemDtoList = itemService.getSearcherItems(
                text,
                PageRequest.of(from / size, size));

        assertThat(itemDtoList).isNotNull();
        assertThat(itemDtoList).isEqualTo(List.of(itemDto));

        verify(itemRepository)
                .findAllAvailableBySearch(anyString(), anyString(), any(Boolean.class), any(Pageable.class));
    }

    @Test
    void createComment_whenCommentIsValid_thenReturnCommentDto() {
        User author = User.builder()
                .id(5L)
                .name("author")
                .email("author@email.com")
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(author)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository
                .findAllByBookerIdAndItemIdAndStatusIsAndEndTimeBefore(
                        anyLong(),
                        anyLong(),
                        any(BookingStatus.class),
                        any(LocalDateTime.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .startTime(LocalDateTime.now().minusHours(2))
                        .endTime(LocalDateTime.now().minusHours(1))
                        .item(item)
                        .booker(author)
                        .status(BookingStatus.APPROVED)
                        .build()));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto savedCommentDto = itemService.create(
                CommentCreateDto.builder()
                        .text("text")
                        .build(),
                author.getId(),
                item.getId());

        assertThat(savedCommentDto).isEqualTo(commentDto);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_whenAuthorNotFound_thenNotFoundExceptionThrown() {
        long authorId = 1L;
        long itemId = 1L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.create(
                        CommentCreateDto.builder()
                                .text("text")
                                .build(),
                        authorId,
                        itemId));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", authorId));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenItemNotFound_thenNotFoundExceptionThrown() {
        User author = User.builder()
                .id(5L)
                .name("author")
                .email("author@email.com")
                .build();
        Long itemId = 10L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.create(
                        CommentCreateDto.builder()
                                .text("text")
                                .build(),
                        author.getId(),
                        itemId));

        assertThat(e.getMessage()).isEqualTo(String.format("Item id=%d not found", itemId));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenBookingNotFound_thenIllegalArgumentExceptionThrown() {
        User author = User.builder()
                .id(5L)
                .name("author")
                .email("author@email.com")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusIsAndEndTimeBefore(
                anyLong(),
                anyLong(),
                any(BookingStatus.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> itemService.create(
                        CommentCreateDto.builder()
                                .text("text")
                                .build(),
                        author.getId(),
                        item.getId()));

        assertThat(e.getMessage())
                .isEqualTo(String.format("User id=%d didn't book this item id=%d", author.getId(), item.getId()));

        verify(commentRepository, never()).save(any(Comment.class));
    }
}