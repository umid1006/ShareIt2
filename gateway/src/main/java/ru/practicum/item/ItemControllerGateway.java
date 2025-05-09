package ru.practicum.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.util.Constants;

@RestController
@RequestMapping("/gateway/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemControllerGateway {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long ownerId,
            @RequestBody @Valid ItemDto itemDto) {
        log.info("Gateway: Add item by user {}", ownerId);
        return itemClient.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long ownerId,
            @PathVariable @Positive Long itemId,
            @RequestBody @Valid ItemDto itemDto) {
        log.info("Gateway: Update item {} by user {}", itemId, ownerId);
        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @PathVariable @Positive Long itemId) {
        log.info("Gateway: Get item {}", itemId);
        return itemClient.getItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Gateway: Get items for user {}", userId);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Gateway: Search items by text '{}'", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(Constants.USER_ID_HEADER) @Positive Long userId,
            @PathVariable @Positive Long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("Gateway: Add comment to item {} by user {}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}