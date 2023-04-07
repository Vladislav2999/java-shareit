package ru.practicum.shareit.item.mapper.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemDtoIn {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;
}