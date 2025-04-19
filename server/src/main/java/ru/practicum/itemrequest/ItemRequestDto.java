// ItemRequestDto.java (DTO - in ru.practicum.request)
package ru.practicum.itemrequest;

import lombok.*;
import ru.practicum.user.UserDto;

import java.time.LocalDateTime;

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
}