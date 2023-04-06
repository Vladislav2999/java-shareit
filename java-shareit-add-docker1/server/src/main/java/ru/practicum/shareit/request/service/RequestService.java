package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {

    List<ItemRequestDto> findAllRequests(Long userId, int from, int size);

    List<ItemRequestDto> findAllByUserId(Long userId);

    ItemRequestDto findByRequestId(Long userId, Long requestId);

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

}
