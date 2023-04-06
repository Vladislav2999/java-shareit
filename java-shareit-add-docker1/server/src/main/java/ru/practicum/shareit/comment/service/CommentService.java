package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.model.dto.CommentDto;

public interface CommentService {

    CommentDto create(CommentDto commentDto, Long itemId, Long userId);
}
