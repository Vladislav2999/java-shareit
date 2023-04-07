package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {

    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive int size) {
        log.info("Gateway-ItemController: запрос списка вещей пользователя с id - {}.", userId);
        return itemClient.getItemsByOwner(userId, from, size);
    }


    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @RequestParam("text") String text,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive int size) {
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        log.info("Gateway-ItemController: запрос поиска вещей по подстроке - {}.", text);
        return itemClient.getItemsByText(text, userId, from, size);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @PathVariable("itemId") Long itemId) {
        log.info("Gateway-ItemController: запрос вещи по id - {} от пользователя с id - {}.", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestBody
            @Valid ItemDto itemDto,
            @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("Gateway-ItemController: запрос создания вещи от пользователя с id - {}.", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @Valid @RequestBody CommentDto commentDto,
            @PathVariable("itemId") Long itemId,
            @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("Gateway-ItemController: запрос создания комментария от пользователя с id - {} для вещи с id - {}.",
                userId,
                itemId
        );
        return itemClient.createComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Gateway-ItemController: запрос обновления вещи с id - {} от пользователя с id - {}.", itemId, userId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }
}

