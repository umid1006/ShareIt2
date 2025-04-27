package ru.practicum.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
}