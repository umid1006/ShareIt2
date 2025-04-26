package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.BookingDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.util.Constants;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingControllerServer {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(
            @RequestHeader(Constants.USER_ID_HEADER) Long userId,
            @RequestBody BookingDto bookingRequestDto) {
        log.info("Server: Creating booking for user {}", userId);
        return bookingService.createBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @PathVariable Long bookingId,
            @RequestHeader(Constants.USER_ID_HEADER) Long userId,
            @RequestParam boolean approved) {
        log.info("Server: Approving booking ID {}", bookingId);
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @PathVariable Long bookingId,
            @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Server: Fetching booking ID {}", bookingId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(
            @RequestHeader(Constants.USER_ID_HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Server: Fetching bookings for user {}", userId);
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(
            @RequestHeader(Constants.USER_ID_HEADER) Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Server: Fetching bookings for owner {}", ownerId);
        return bookingService.getOwnerBookings(ownerId, state);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException ex) {
        return ex.getMessage();
    }
}