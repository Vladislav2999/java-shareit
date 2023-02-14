package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Item item);

    Item update(Item item, Long userId, Long itemId);

    Item getById(Long userId, Long itemId);

    List<Item> getByUser(Long userId);

    List<Item> searchItem(String text);

}
