package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.util.Constants;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemControllerServer {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ru.practicum.dto.ItemDto createItem(
            @RequestHeader(Constants.USER_ID_HEADER) Long ownerId,
            @RequestBody ItemDto itemDto) {
        log.info("Server: Creating item for owner {}", ownerId);
        return itemService.add(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ru.practicum.dto.ItemDto updateItem(
            @RequestHeader(Constants.USER_ID_HEADER) Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Server: Updating item ID {}", itemId);
        return itemService.updateInStorage(itemDto, ownerId, itemId);
    }

    @GetMapping("/{itemId}")
    public ru.practicum.dto.ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Server: Fetching item ID {}", itemId);
        return itemService.getItemById(itemId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addCommentToItem(
            @RequestHeader(Constants.USER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        log.info("Server: Adding comment to item {}", itemId);
        return itemService.addComment(itemId, userId, commentDto);
    }
}