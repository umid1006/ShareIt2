package ru.practicum.itemrequest;

import lombok.*;

@Data
@Builder
public class ItemResponseDto {
    private Long id;
    private String name;
    private Long ownerId;
}