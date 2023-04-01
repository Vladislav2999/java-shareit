package ru.practicum.shareit.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Mapper(componentModel = "spring", uses = {UserService.class, ItemService.class})

public interface CommentMapper {


    CommentDto toCommentDto(Comment comment);


    Comment toComment(CommentDto commentDto);
}