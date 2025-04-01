// ItemDto.java
package ru.practicum.item;

import lombok.*;
import ru.practicum.booking.BookingDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ItemDto {
    Long id;
    String name;
    String description;
    Long ownerId; // Keep this for the controller's input
    Boolean available;
    Long requestId; // Keep this
    BookingDto lastBooking; // Добавляем поле для последнего бронирования
    BookingDto nextBooking; // Добавляем поле для следующего бронирования
}