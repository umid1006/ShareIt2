package ru.practicum.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;
import ru.practicum.util.Constants;

@RestController
@RequestMapping("/gateway/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserControllerGateway {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        log.info("Gateway: Get all users (from={}, size={}) by user {}",
                from, size, userId);
        return userClient.getAllUsers(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(
            @RequestHeader(defaultValue = Constants.USER_ID_HEADER) @Positive Long userId,
            @RequestBody @Valid UserDto userDto) {
        log.info("Gateway: Create new user");
        return userClient.createUser(userDto);
    }

    @GetMapping("/{targetUserId}")
    public ResponseEntity<Object> getUserById(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long targetUserId) {

        log.info("Gateway: Get user {} by admin {}", targetUserId, userId);
        return userClient.getUserById(userId, targetUserId);
    }

    @PatchMapping("/{targetUserId}")
    public ResponseEntity<Object> updateUser(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long targetUserId,
            @RequestBody @Valid UserDto userDto) {

        log.info("Gateway: Update user {} by admin {}", targetUserId, userId);
        return userClient.updateUser(targetUserId, userDto);
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Object> deleteUser(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long targetUserId) {

        log.info("Gateway: Delete user {} by admin {}", targetUserId, userId);
        return userClient.deleteUser(userId, targetUserId);
    }
}