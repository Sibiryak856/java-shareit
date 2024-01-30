package ru.practicum.shareit.item;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemDtoConverter {

    @Autowired
    private ModelMapper modelMapper;

    public ItemDto convertToItemDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    public Item convertToItem(ItemDto itemDto) {
        return modelMapper.map(itemDto, Item.class);
    }

}
