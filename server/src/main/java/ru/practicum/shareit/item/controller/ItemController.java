package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    public List<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Min(0) @RequestParam(value = "from", defaultValue = "0") int offset,
                                      @Min(1) @RequestParam(value = "size", defaultValue = "10") int limit) {
        log.info("Request received: GET /items for user id= {}", userId);
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.ASC, "id"));
        List<ItemDto> items = itemService.getAllByOwner(userId, pageable);
        log.info("Request GET /items processed: {}", items);
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Request received: GET /items/id={}", itemId);
        ItemDto item = itemService.getItem(itemId, userId);
        log.info("Request GET /items/id processed: {}", item);
        return item;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemCreateDto itemDto) {
        log.info("Request received: POST /items: {}", itemDto);
        ItemDto createdItem = itemService.create(itemDto, userId);
        log.info("Request POST /items processed: item={} is created", createdItem);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Request received: PATCH /items: id={}", itemId);
        ItemDto updatedItem = itemService.update(itemId, userId, itemUpdateDto);
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
    public List<ItemDto> search(@RequestParam String text,
                                @Min(0) @RequestParam(value = "from", defaultValue = "0") int offset,
                                @Min(1) @RequestParam(value = "size", defaultValue = "10") int limit) {
        log.debug("Request received: GET /items/search");
        Pageable pageable = PageRequest.of(offset / limit, limit);
        List<ItemDto> searchedItems = itemService.getSearcherItems(text, pageable);
        log.debug("Request GET /items/search processed: searchedItems: {}", searchedItems);
        return searchedItems;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable Long itemId,
                             @Valid @RequestBody CommentCreateDto commentDto) {
        log.debug("Request received: POST /items/{itemId}/comment: {}", commentDto);
        CommentDto createdComment = itemService.create(commentDto, userId, itemId);
        log.info("Request POST /items/{itemId}/comment processed: comment={} is created", createdComment);
        return createdComment;
    }
}
