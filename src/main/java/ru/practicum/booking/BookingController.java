package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                            @RequestBody BookingDto bookingRequestDto) {
        log.info("POST /bookings: Create booking {} by user {}", bookingRequestDto, userId);
        try {
            BookingDto createdBooking = bookingService.createBooking(bookingRequestDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (ValidateException | NotFoundException e) {
            log.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable Long bookingId,
                                                            @RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                            @RequestParam boolean approved) {
        log.info("PATCH /bookings/{}: Update booking {}, approved={}", bookingId, bookingId, approved);
        try {
            BookingDto updatedBooking = bookingService.updateBooking(bookingId, userId, approved);
            return ResponseEntity.ok(updatedBooking);
        } catch (NotFoundException e) {
            log.error("Error updating booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ValidateException e) {
            log.error("Error updating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long bookingId,
                                                         @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("GET /bookings/{}: Get booking {}", bookingId, bookingId);
        try {
            BookingDto booking = bookingService.getBooking(bookingId, userId);
            return ResponseEntity.ok(booking);
        } catch (NotFoundException e) {
            log.error("Error getting booking by id: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookings(
            @RequestHeader(Constants.USER_ID_HEADER) Long userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("GET /bookings?state={}: Get all bookings for booker {}", state, userId);
        try {
            List<BookingDto> bookings = bookingService.getBookings(userId, state);
            return ResponseEntity.ok(bookings);
        } catch (NotFoundException e) {
            log.error("Error getting bookings for booker: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(
            @RequestHeader(Constants.USER_ID_HEADER) Long ownerId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner?state={}: Get all bookings for item's owner {}", state, ownerId);
        try {
            List<BookingDto> bookings = bookingService.getOwnerBookings(ownerId, state);
            return ResponseEntity.ok(bookings);
        } catch (NotFoundException e) {
            log.error("Error getting bookings for item owner: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}