package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

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
    public Collection<Item> getAll() {
        log.info("Request received: GET /items");
        Collection<Item> items = itemService.getAll();
        log.info("Request GET /items processed: {}", items);
        return items;
    }

    @GetMapping("/{id}")
    public Item getItem(@PathVariable Long id) {
        log.info("Request received: GET /items/id={}", id);
        Item item = itemService.getItem(id);
        log.info("Request GET /items/id processed: {}", item);
        return item;
    }

    @PostMapping
    public Item create(@Valid @RequestBody Item item) {
        log.info("Request received: POST /items: {}", item);
        Item createdItem = itemService.create(item);
        log.info("Request POST /items processed: item={} is created", createdItem);
        return createdItem;
    }

    @PatchMapping
    public Item update(@Valid @RequestBody Item item) {
        log.info("Request received: PATCH /items: {}", item);
        Item updatedItem = itemService.update(item);
        log.info("Request PUT /items processed: item: {} is updated", updatedItem);
        return updatedItem;
    }

    @DeleteMapping
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}
