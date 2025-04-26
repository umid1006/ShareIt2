package ru.practicum.booking;

import ru.practicum.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingRequestDto, Long bookerId);

    BookingDto updateBooking(Long bookingId, Long userId, boolean approved);

    BookingDto cancelBooking(Long bookingId, Long userId); // New method

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getBookings(Long userId, String state);

    List<BookingDto> getOwnerBookings(Long ownerId, String state);
}