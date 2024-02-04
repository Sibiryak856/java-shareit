package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponseDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received: GET /items for user id= {}", userId);
        List<Item> items = itemService.getAllByUser(userId);
        log.info("Request GET /items processed: {}", items);
        return items.stream()
                .map(i -> ItemMapper.INSTANCE.toItemDto(i))
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@PathVariable Long itemId,
                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received: GET /items/id={}", itemId);
        Item item = itemService.getItem(itemId);
        log.info("Request GET /items/id processed: {}", item);
        return ItemMapper.INSTANCE.toItemDto(item);
    }

    @PostMapping
    public ItemResponseDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Valid @RequestBody ItemCreateDto itemDto) {
        log.info("Request received: POST /items: {}", itemDto);
        Item createdItem = itemService.create(
                ItemMapper.INSTANCE.toItem(itemDto),
                userId);
        log.info("Request POST /items processed: item={} is created", createdItem);
        return ItemMapper.INSTANCE.toItemDto(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@PathVariable Long itemId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Request received: PATCH /items: id={}", itemId);
        Item updatedItem = itemService.update(itemId, userId, itemUpdateDto);
        log.info("Request PATCH /items processed: item: {} is updated", updatedItem);
        return ItemMapper.INSTANCE.toItemDto(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received: DELETE /items/id={} for user id={}", itemId, userId);
        itemService.delete(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestParam String text) {
        log.debug("Request received: GET /items/search");
        List<Item> searchedItems = itemService.getSearcherItems(text);
        log.debug("Request GET /items/search processed: searchedItems: {}", searchedItems);
        return searchedItems.stream()
                .map(i -> ItemMapper.INSTANCE.toItemDto(i))
                .collect(Collectors.toList());
    }
}
