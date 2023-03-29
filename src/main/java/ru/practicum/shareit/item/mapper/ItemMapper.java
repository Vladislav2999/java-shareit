
package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDtoIn;
import ru.practicum.shareit.item.model.dto.ItemDtoOut;

@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public interface ItemMapper {

 ItemDtoOut toItemDtoOut(Item item);

 Item toItem(ItemDtoIn itemDto);
}