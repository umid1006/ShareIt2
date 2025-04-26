package ru.practicum.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.BookItemRequestDto;
import ru.practicum.util.BookingState;
import ru.practicum.util.Constants;

@RestController
@RequestMapping(path = "/gateway/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingControllerGateway {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        log.info("Gateway: Get bookings for user {} (state={}, from={}, size={})",
                userId, state, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @RequestBody @Valid BookItemRequestDto requestDto) {

        log.info("Gateway: Create booking for user {} (itemId={}, dates={}-{})",
                userId, requestDto.getItemId(), requestDto.getStart(), requestDto.getEnd());
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long bookingId) {

        log.info("Gateway: Get booking {} for user {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}