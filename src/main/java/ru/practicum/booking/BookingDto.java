// booking/BookingDto.java
package ru.practicum.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;  // Foreign key to Item
    private Long bookerId; // Foreign key to User
    private Booking.BookingStatus status; // Use the enum from the Booking entity
}