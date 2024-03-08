package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    public ItemRequestRepository requestRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private RequestMapper requestMapper;
    private ItemMapper itemMapper;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, ItemRepository itemRepository, UserRepository userRepository, RequestMapper requestMapper, ItemMapper itemMapper) {
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.requestMapper = requestMapper;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemRequestDto create(ItemRequestCreateDto request, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        ItemRequest itemRequest = requestMapper.toRequest(request, user, LocalDateTime.now());
        return requestMapper.toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findAllByUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        List<ItemRequest> itemRequests = requestRepository
                .findAllByRequestorId(userId, Sort.by("created").descending());

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<ItemDto> itemsWithRequest = itemMapper.toListItemDto(itemRepository.findAllByRequestIdIn(requestIds));

        Map<Long, List<ItemDto>> requestItemsMap = itemsWithRequest.stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requestMapper.toRequestDtosList(itemRequests, requestItemsMap);
    }

    @Override
    public List<ItemRequestDto> findAll(long userId, Pageable pageable) {
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorIdNot(
                        userId, pageable);

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<ItemDto> itemDtoWithRequest = itemMapper.toListItemDto(itemRepository.findAllByRequestIdIn(requestIds));

        Map<Long, List<ItemDto>> requestItemsMap = itemDtoWithRequest.stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requestMapper.toRequestDtosList(itemRequests, requestItemsMap);
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        ItemRequestDto itemRequest = requestMapper.toRequestDto(requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("ItemRequest id=%d not found", requestId))));
        List<ItemDto> requestItems = itemMapper.toListItemDto(itemRepository.findAllByRequestIdIn(List.of(requestId)));
        itemRequest.setItems(requestItems);
        return itemRequest;
    }
}
