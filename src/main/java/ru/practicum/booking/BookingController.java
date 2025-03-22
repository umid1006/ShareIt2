// booking/BookingController.java
package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody BookingDto bookingDto) {
        log.info("POST /bookings: Create booking {} by user {}", bookingDto, userId);
        try {
            BookingDto createdBooking = bookingService.createBooking(bookingDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking); // Return 201 Created
        } catch (ValidateException | NotFoundException e) {
            // Handle validation and not found exceptions, returning a 400 or 404
            log.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null); // Or return a custom error object
        }
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable Long bookingId,
                                                    @RequestBody BookingDto bookingDto) {
        log.info("PATCH /bookings/{}: Update booking {}", bookingId, bookingDto);
        try{
            BookingDto updatedBooking = bookingService.updateBooking(bookingId, bookingDto);
            return ResponseEntity.ok(updatedBooking);
        } catch (NotFoundException e){
            log.error("Error updating booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ValidateException e) {
            log.error("Error updating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId) {
        log.info("GET /bookings/{}: Get booking {}", bookingId, bookingId);
        try {
            BookingDto booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(booking);
        } catch (NotFoundException e) {
            log.error("Error getting booking by id: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 for not found
        }

    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("GET /bookings: Get all bookings for booker {}", bookerId);
        try {
            List<BookingDto> bookings = bookingService.getAllBookingsByBooker(bookerId);
            return ResponseEntity.ok(bookings);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingsByItemOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("GET /bookings/owner: Get all bookings for item's owner {}", ownerId);

        try {
            List<BookingDto> bookings = bookingService.getAllBookingsByItemOwner(ownerId);
            return ResponseEntity.ok(bookings);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/{bookingId}/approve")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam Boolean approved) {
        log.info("PATCH /bookings/{}/approve?approved={}: Approve booking {} by user {}", bookingId, approved, bookingId, ownerId);
        try{
            BookingDto updatedBooking = bookingService.approveBooking(bookingId, ownerId, approved);
            return ResponseEntity.ok(updatedBooking);
        } catch (NotFoundException e) {
            log.error("Error approving booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ValidateException e){
            log.error("Error approving booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/all") //for testing
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        log.info("GET /bookings/all: Get all bookings");
        List<BookingDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // Centralized Exception Handling (Better with @ControllerAdvice, but this is simpler for demonstration)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        log.error("NotFoundException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<Map<String, String>> handleValidateException(ValidateException e) {
        log.error("ValidateException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e){
        log.error("Exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}