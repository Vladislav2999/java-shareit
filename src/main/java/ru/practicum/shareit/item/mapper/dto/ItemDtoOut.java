package ru.practicum.shareit.item.mapper.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.comment.model.dto.CommentDto;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoOut {
    private Long id;

    private String name;

    private String description;

    private Long ownerId;

    private Boolean available;

    private Long requestId;

    private List<CommentDto> comments;

    private BookingDtoOut lastBooking;

    private BookingDtoOut nextBooking;
}
