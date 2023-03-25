package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final RequestService itemRequestService;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequests(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive int size) {
        return itemRequestService.findAllRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUserId(@RequestHeader(SHARER_USER_ID) Long userId) {
        return itemRequestService.findAllByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findByRequestId(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @PathVariable Long requestId) {
        return itemRequestService.findByRequestId(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @Validated
            @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }
}
