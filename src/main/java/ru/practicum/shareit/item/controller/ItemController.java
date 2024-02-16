package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

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
        List<ItemResponseDto> items = itemService.getAllByOwner(userId);
        log.info("Request GET /items processed: {}", items);
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@PathVariable Long itemId,
                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received: GET /items/id={}", itemId);
        ItemResponseDto item = itemService.getItem(itemId, userId);
        log.info("Request GET /items/id processed: {}", item);
        return item;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @Valid @RequestBody ItemCreateDto itemDto) {
        log.info("Request received: POST /items: {}", itemDto);
        ItemResponseDto createdItem = itemService.create(itemDto, userId);
        log.info("Request POST /items processed: item={} is created", createdItem);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@PathVariable Long itemId,
                                  @RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Request received: PATCH /items: id={}", itemId);
        ItemResponseDto updatedItem = itemService.update(itemId, userId, itemUpdateDto);
        log.info("Request PATCH /items processed: item: {} is updated", updatedItem);
        return updatedItem;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received: DELETE /items/id={} for user id={}", itemId, userId);
        itemService.delete(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestParam String text) {
        log.debug("Request received: GET /items/search");
        List<ItemResponseDto> searchedItems = itemService.getSearcherItems(text);
        log.debug("Request GET /items/search processed: searchedItems: {}", searchedItems);
        return searchedItems;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{itemId}/comment")
    public CommentResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable Long itemId,
                                     @Validated @RequestBody CommentCreateDto commentDto) {
        log.debug("Request received: POST /items/{itemId}/comment: {}", commentDto);
        CommentResponseDto createdComment = itemService.create(commentDto, userId, itemId);
        log.info("Request POST /items/{itemId}/comment processed: comment={} is created", createdComment);
        return createdComment;
    }
}
