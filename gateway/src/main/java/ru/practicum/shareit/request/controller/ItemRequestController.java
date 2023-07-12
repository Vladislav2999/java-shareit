package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@RequestMapping("/requests")
@Slf4j
@Validated
public class ItemRequestController {

    public static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive int size) {
        log.info("Gateway-ItemRequestController: запрос списка всех доступных запросов от пользователя с id - {}.",
                userId
        );
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("Gateway-ItemRequestController: запрос списка всех запросов у пользователя с id - {}.", userId);
        return itemRequestClient.getItemRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @PathVariable Long requestId) {
        log.info("Gateway-ItemRequestController: запрос поиска запроса по id - {} от пользователя с id - {}.",
                requestId, userId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }


    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @RequestBody
            @Valid
            RequestDto requestDto) {
        log.info("Gateway-ItemRequestController: запрос создания запроса от пользователя с id - {}.", userId);
        return itemRequestClient.createItemRequest(userId, requestDto);
    }
}

