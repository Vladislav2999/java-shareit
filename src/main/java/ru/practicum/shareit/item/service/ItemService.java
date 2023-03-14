package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.dto.ItemDtoOut;

import java.util.List;

public interface ItemService {
    ItemDtoOut create(ItemDtoIn itemDto, Long userId);

    ItemDtoOut update(ItemDtoIn itemDto, Long userId);

    ItemDtoOut getById(Long itemId, Long ownerId);

    List<ItemDtoOut> getAll(Long userId);

    List<ItemDtoOut> getByText(String text,  Long ownerId);

    Item getItemById(Long itemId);
}
