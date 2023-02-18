package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestBody @Valid ItemDto itemDto,
                       @RequestHeader(SHARER_USER_ID) Long userId) {
        return ItemMapper.toItemDto( itemService.create(itemDto, userId));


    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(SHARER_USER_ID) Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable("itemId") Long itemId) {
        return ItemMapper.toItemDto(itemService
                .update(itemDto, userId, itemId));
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@RequestHeader(SHARER_USER_ID) Long userId,
                               @PathVariable("itemId") Long itemId) {
        return ItemMapper.toItemDto(itemService.getItemById(userId, itemId));
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(SHARER_USER_ID) Long userId) {
        return itemService.getItemByUserId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestHeader(SHARER_USER_ID) Long userId,
                                        @RequestParam("text") String text) {
        return itemService.getByText(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
