package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ItemResponseDto {
    private Long id;
    private String name;
    private Long ownerId;
}