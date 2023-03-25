package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.dto.ItemDtoOut;

import java.util.List;

public interface ItemService {
    ItemDtoOut create(ItemDtoIn itemDto, Long userId);

    ItemDtoOut update(ItemDtoIn itemDto, Long userId);

    ItemDtoOut getByItemIdAndUserId(Long itemId, Long ownerId);

    List<ItemDtoOut> getAll(Long userId, int from, int size);

    List<ItemDtoOut> getByText(String text,  Long ownerId, int from, int size);

    Item getItemById(Long itemId);
}
