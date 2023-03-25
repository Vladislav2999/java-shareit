package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.model.dto.ItemDtoOut;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotBlank
    private String description;

    private LocalDateTime created;

    private List<ItemDtoOut> items;
}
