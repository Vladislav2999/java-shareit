package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    public Item create(ItemDto itemDto, Long userId) {
        return itemRepository.create(ItemMapper.toItem(itemDto, userService.getUserById(userId)));
    }

    public Item update(ItemDto itemDto, Long userId, Long itemId) {
        return itemRepository.update(ItemMapper.toItem(itemDto, userService.getUserById(userId)), userId, itemId);
    }

    public Item getItemById(Long userId, Long itemId) {
        return itemRepository.getById(userId, itemId);
    }

    public List<Item> getItemByUserId(Long userId) {
        return itemRepository.getByUser(userId);
    }

    public List<Item> getByText(String text) {
        return itemRepository.searchItem(text);
    }
}
