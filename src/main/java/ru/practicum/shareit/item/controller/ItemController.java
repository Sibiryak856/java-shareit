package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ModelMapper modelMapper;

    /*@Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }*/

    @GetMapping
    public List<Item> getAll() {
        log.info("Request received: GET /items");
        List<Item> items = itemService.getAll();
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
    public Item create(@RequestHeader("X-Later-User-Id") @NotNull @Min(1) Long userId,
                       @Valid @RequestBody Item item) {
        log.info("Request received: POST /items: {}", item);
        Item createdItem = itemService.create(item, userId);
        log.info("Request POST /items processed: item={} is created", createdItem);
        return createdItem;
    }

    @PatchMapping
    public Item update(@RequestHeader("X-Later-User-Id") @NotNull @Min(1) Long ownerId,
                       @Valid @RequestBody ItemDto itemDto) {
        log.info("Request received: PATCH /items: {}", itemDto);
        Item updatedItem = itemService.update(modelMapper.map(itemDto, Item.class), ownerId);
        log.info("Request PUT /items processed: item: {} is updated", updatedItem);
        return updatedItem;
    }

    private Item convertToDto(Item item) {
        modelMapper.typeMap(Item.class, Item.class).addMappings(mapper -> mapper.skip(Item::setOwner));
        return modelMapper.map(item, Item.class);
    }

    @DeleteMapping
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}
