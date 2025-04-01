// BookingController.java
package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;
import ru.practicum.util.Constants; // Import the Constants class

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                    @RequestBody BookingDto bookingDto) {
        log.info("POST /bookings: Create booking {} by user {}", bookingDto, userId);
        try {
            BookingDto createdBooking = bookingService.createBooking(bookingDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (ValidateException | NotFoundException e) {
            log.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable Long bookingId,
                                                    @RequestBody BookingDto bookingDto) {
        log.info("PATCH /bookings/{}: Update booking {}", bookingId, bookingDto);
        try {
            BookingDto updatedBooking = bookingService.updateBooking(bookingId, bookingDto);
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
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId) {
        log.info("GET /bookings/{}: Get booking {}", bookingId, bookingId);
        try {
            BookingDto booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(booking);
        } catch (NotFoundException e) {
            log.error("Error getting booking by id: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookingsByBooker(
            @RequestHeader(Constants.USER_ID_HEADER) Long bookerId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("GET /bookings?state={}: Get all bookings for booker {}", state, bookerId);
        try {
            List<BookingDto> bookings = bookingService.getAllBookingsByBooker(bookerId, state);
            return ResponseEntity.ok(bookings);
        } catch (NotFoundException e) {
            log.error("Error getting bookings for booker: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingsByItemOwner(
            @RequestHeader(Constants.USER_ID_HEADER) Long ownerId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner?state={}: Get all bookings for item's owner {}", state, ownerId);
        try {
            List<BookingDto> bookings = bookingService.getAllBookingsByItemOwner(ownerId, state);
            return ResponseEntity.ok(bookings);
        } catch (NotFoundException e) {
            log.error("Error getting bookings for item owner: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/{bookingId}/approve")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader(Constants.USER_ID_HEADER) Long ownerId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam Boolean approved) {
        log.info("PATCH /bookings/{}/approve?approved={}: Approve booking {} by user {}", bookingId, approved, bookingId, ownerId);
        try {
            BookingDto updatedBooking = bookingService.approveBooking(bookingId, ownerId, approved);
            return ResponseEntity.ok(updatedBooking);
        } catch (NotFoundException e) {
            log.error("Error approving booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ValidateException e) {
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
}