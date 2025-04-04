package ru.practicum.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.item.ItemDto;
import ru.practicum.user.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {

    private Long id;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be in present or future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    // Для создания бронирования
    @NotNull(message = "Item ID cannot be null")
    private Long itemId;

    // Только для ответа
    private ItemDto item;

    // Только для ответа
    private UserDto booker;

    // Только для ответа
    private Long bookerId;

    // Только для ответа
    private BookingStatus status;
}