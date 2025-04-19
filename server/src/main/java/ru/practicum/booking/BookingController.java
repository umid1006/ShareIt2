package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;
import ru.practicum.util.Constants;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(
            @RequestHeader(Constants.USER_ID_HEADER) Long userId,
            @RequestBody BookingDto bookingRequestDto) {
        log.info("POST /bookings: Create booking {} by user {}", bookingRequestDto, userId);
        return bookingService.createBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(
            @PathVariable Long bookingId,
            @RequestHeader(Constants.USER_ID_HEADER) Long userId,
            @RequestParam boolean approved) {
        log.info("PATCH /bookings/{}: Update booking {}, approved={}",
                bookingId, bookingId, approved);
        return bookingService.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @PathVariable Long bookingId,
            @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("GET /bookings/{}: Get booking {}", bookingId, bookingId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookings(
            @RequestHeader(Constants.USER_ID_HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("GET /bookings?state={}: Get bookings for user {}", state, userId);
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(
            @RequestHeader(Constants.USER_ID_HEADER) Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner?state={}: Get bookings for owner {}", state, ownerId);
        return bookingService.getOwnerBookings(ownerId, state);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ValidateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(ValidateException ex) {
        return ex.getMessage();
    }
}