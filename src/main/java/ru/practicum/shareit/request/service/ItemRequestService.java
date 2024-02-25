package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestCreateDto request, long userId);

    List<ItemRequestDto> findAllByUser(long userId);

    List<ItemRequestDto> findAll(long userId, int from, int size);

    ItemRequestDto findById(Long userId, Long requestId);

}
