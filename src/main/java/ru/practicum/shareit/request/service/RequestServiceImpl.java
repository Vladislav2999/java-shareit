package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptionHandler.exception.EntityNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final RequestMapper requestMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> findAllRequests(Long userId, int from, int size) {
        log.info("Запрос списка всех доступных запросов от пользователя с id " + userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id " + userId + " не найден")));

        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdNot(userId, PageRequest.of((from / size), size));
        return getRequestDtoList(itemRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> findAllByUserId(Long userId) {
        log.info("Запрос списка всех запросов у пользователя с id " + userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id " + userId + " не найден")));

        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedAsc(userId);
        return getRequestDtoList(itemRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto findByRequestId(Long userId, Long requestId) {
        log.info("Запрос поиска запроса по id от пользователя с id " + userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id " + userId + " не найден")));
        ItemRequest itemRequest = itemRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос вещи с Id " + requestId + " не найден"));

        ItemRequestDto itemRequestDto = requestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequest.getId())
                .stream()
                .map(itemMapper::toItemDtoOut)
                .collect(toList()));
        return itemRequestDto;
    }

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Запрос создания запроса от пользователя с id " + userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + " не найден"));
        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);
        return requestMapper.toItemRequestDto(itemRequest);
    }

    private List<ItemRequestDto> getRequestDtoList(List<ItemRequest> itemRequests) { // переводит в List<ItemRequestDto> и расставляет в них нужные items
        Map<ItemRequest, List<Item>> itemsAll = itemRepository
                .findAllByRequestIn(itemRequests)
                .stream()
                .collect(Collectors.groupingBy(Item::getRequest, toList()));

        return itemRequests
                .stream()
                .map(itemRequest -> {
                    ItemRequestDto request = requestMapper.toItemRequestDto(itemRequest);
                    List<Item> itemList = itemsAll.getOrDefault(itemRequest, Collections.emptyList());
                    request.setItems(itemList
                            .stream()
                            .map(itemMapper::toItemDtoOut)
                            .collect(toList()));
                    return request;
                })
                .collect(toList());
    }
}
