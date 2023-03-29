package ru.practicum.shareit.comment.mapper;

import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Mapper(componentModel = "spring", uses = {UserService.class, ItemService.class})
public interface CommentMapper {

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toComment(CommentDto commentDto);
}
