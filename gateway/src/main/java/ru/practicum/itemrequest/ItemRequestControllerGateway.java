package ru.practicum.itemrequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.util.Constants;

@RestController
@RequestMapping(path = "/gateway/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestControllerGateway {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Gateway: Creating request from user {}", userId);
        return itemRequestClient.createRequest(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long requestId) {
        log.info("Gateway: Get request {} for user {}", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId) {
        log.info("Gateway: Get all requests for user {}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Gateway: Get all requests (from={}, size={}) for user {}", from, size, userId);
        return itemRequestClient.getAllRequests(userId, from, size);
    }
}