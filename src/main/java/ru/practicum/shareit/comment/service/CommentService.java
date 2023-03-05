package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.model.dto.CommentDto;

public interface CommentService {

    CommentDto save(CommentDto commentDto, Long itemId, Long userId);
}
