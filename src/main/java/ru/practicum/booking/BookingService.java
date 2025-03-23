// booking/BookingService.java
package ru.practicum.booking;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Long userId);
    BookingDto updateBooking(Long bookingId, BookingDto bookingDto);
    BookingDto getBookingById(Long bookingId);
    List<BookingDto> getAllBookingsByBooker(Long bookerId);
    List<BookingDto> getAllBookingsByItemOwner(Long ownerId); // New method
    BookingDto approveBooking(Long bookingId, Long ownerId, Boolean approved); // New Method
    List<BookingDto> getAllBookings(); //for testing
}