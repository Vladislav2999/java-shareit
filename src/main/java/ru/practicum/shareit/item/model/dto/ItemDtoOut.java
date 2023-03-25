package ru.practicum.shareit.item.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.comment.model.dto.CommentDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemDtoOut {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Booking lastBooking;

    private Booking nextBooking;

    private List<CommentDto> comments;

    private Long requestId;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Booking {
        private Long id;
        private Long bookerId;
    }

    public ItemDtoOut(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
