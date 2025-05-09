// ItemRequestDto.java (DTO - in ru.practicum.request)
package ru.practicum.itemrequest;

import lombok.*;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private List<ItemDto> items; // Это поле должно существовать
}