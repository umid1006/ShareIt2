package ru.practicum.booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.item.ItemDto;
import ru.practicum.user.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;  // Для создания бронирования
    ItemDto item;  // Только для ответа
    UserDto booker;  // Только для ответа
    Long bookerId;  // Только для ответа
    BookingStatus status;  // Только для ответа
}