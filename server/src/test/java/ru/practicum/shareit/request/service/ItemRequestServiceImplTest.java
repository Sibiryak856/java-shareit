package ru.practicum.shareit.request.service;

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
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private ItemRequestCreateDto requestCreateDto;
    private ItemRequestDto requestDto;
    private ItemRequest request;
    private User requestor;

    @BeforeEach
    void setUp() {
        LocalDateTime created = LocalDateTime.now().withNano(0);
        requestCreateDto = ItemRequestCreateDto.builder()
                .description("description")
                .build();
        requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(created)
                .items(null)
                .build();
        requestor = User.builder()
                .id(1L)
                .name("name")
                .email("name@email.com")
                .build();
        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requestor)
                .created(created)
                .build();
    }

    @AfterEach
    void clean() {
        requestCreateDto = null;
        requestDto = null;
        request = null;
        requestor = null;
    }


    @Test
    void create_whenRequestIsValid_thenReturnItemRequestDto() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);

        ItemRequestDto savedRequest = requestService.create(requestCreateDto, requestor.getId());

        assertThat(savedRequest).isEqualTo(requestDto);
    }

    @Test
    void create_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 10L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.create(requestCreateDto, userId));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));

        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void findAllByUser_whenItemsFound_thenReturnRequestWithItems() {
        requestDto.setItems(List.of(ItemDto.builder()
                .id(1L)
                .requestId(1L)
                .build()));
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(requestRepository.findAllByRequestorId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(List.of(Item.builder()
                        .id(1L)
                        .requestId(1L)
                        .build()));

        List<ItemRequestDto> itemRequestDtos = requestService.findAllByUser(requestor.getId());

        assertThat(itemRequestDtos).isEqualTo(List.of(requestDto));
    }

    @Test
    void findAllByUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 10L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(FALSE);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.findAllByUser(userId));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));

        verify(requestRepository, never()).findAllByRequestorId(anyLong(), any(Sort.class));
    }

    @Test
    void findAll_whenRequestIsValid_thenReturnRequestDtoList() {
        int from = 5;
        int size = 10;
        requestDto.setItems(List.of(ItemDto.builder()
                .id(1L)
                .requestId(1L)
                .build()));
        when(requestRepository.findAllByRequestorIdNot(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(List.of(Item.builder()
                        .id(1L)
                        .requestId(1L)
                        .build()));

        List<ItemRequestDto> itemRequestDtoList = requestService.findAll(
                requestor.getId(),
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")));

        assertThat(itemRequestDtoList).isEqualTo(List.of(requestDto));
    }

    @Test
    void findById_whenUserAndRequestFound_thenReturnItemRequestDto() {
        requestDto.setItems(Collections.emptyList());
        when(userRepository.existsById(anyLong()))
                .thenReturn(TRUE);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestIdIn(List.of(request.getId())))
                .thenReturn(Collections.emptyList());

        ItemRequestDto itemRequestDto = requestService.findById(requestor.getId(), request.getId());

        assertThat(itemRequestDto).isEqualTo(requestDto);
    }

    @Test
    void findById_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 10L;
        Long requestId = 1L;
        when(userRepository.existsById(anyLong()))
                .thenReturn(FALSE);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.findById(userId, requestId));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));

        verify(requestRepository, never()).findById(requestId);
    }

    @Test
    void findById_whenRequestNotFound_thenNotFoundExceptionThrown() {
        Long userId = 1L;
        Long requestId = 10L;
        when(userRepository.existsById(userId))
                .thenReturn(TRUE);
        when(requestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.findById(userId, requestId));

        assertThat(e.getMessage()).isEqualTo(String.format("ItemRequest id=%d not found", requestId));
    }
}