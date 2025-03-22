// UserController.java (Corrected - NO try-catch in create)
package ru.practicum.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        log.info("Received POST request to create User: {}", userDto);
        // NO try-catch HERE! Let exceptions propagate.
        User user = userMapper.toUser(userDto);
        UserDto createdUserDto = userMapper.toUserDto(userService.saveUser(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Received PATCH request to update user ID {}: {}", userId, userDto);
        User user = userMapper.toUser(userDto);
        UserDto updatedUserDto = userMapper.toUserDto(userService.updateUser(user, userId));
        return ResponseEntity.ok(updatedUserDto);

    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> get(@PathVariable Long userId) {
        log.info("Received GET request to retrieve user ID {}", userId);
        UserDto userDto = userMapper.toUserDto(userService.getUserById(userId));
        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        log.info("Received GET request to retrieve all users");
        List<UserDto> userDtos = userService.getAllUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        log.info("Received DELETE request to delete user ID {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}