package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
        List<ItemRequestDto> itemRequests = requestMapper.toRequestDtosList(requestRepository
                .findAllByRequestorId(userId, Sort.by("created").descending()));


        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());

        List<Item> itemsWithRequest = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> requestItemsMap = itemsWithRequest.stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        itemRequests.forEach(itemRequestDto ->
                itemRequestDto.setItems(itemMapper.toListItemDto(
                        requestItemsMap.getOrDefault(itemRequestDto.getId(), Collections.emptyList()))));

        return itemRequests;
    }

    @Override
    public List<ItemRequestDto> findAll(long userId, int from, int size) {
        List<ItemRequestDto> itemRequests = requestMapper.toRequestDtosList(
                requestRepository.findAllByRequestorIdNot(
                        userId, PageRequest.of(from/size, size, Sort.by(Sort.Direction.DESC, "created"))));

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());

        List<Item> itemsWithRequest = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> requestItemsMap = itemsWithRequest.stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        itemRequests.forEach(itemRequestDto ->
                itemRequestDto.setItems(itemMapper.toListItemDto(
                        requestItemsMap.getOrDefault(itemRequestDto.getId(), Collections.emptyList()))));

        return itemRequests;
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
