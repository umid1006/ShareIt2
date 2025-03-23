// item/ItemController.java (Changes Needed)
package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.util.Constants; // Import the Constants class

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(Constants.USER_ID_HEADER) Long ownerId, @RequestBody ItemDto itemDto) {
        log.info("POST /items: Add item {} by user {}", itemDto, ownerId);
        try {
            ItemDto addedItem = itemService.add(itemDto, ownerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedItem);
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(Constants.USER_ID_HEADER) Long ownerId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/{}: Update item {} by user {}", itemId, itemDto, ownerId);
        try{
            return ResponseEntity.ok(itemService.updateInStorage(itemDto, ownerId, itemId));
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        log.info("GET /items/{}: Get item {}", itemId, itemId);
        try{
            return ResponseEntity.ok(itemService.getItemById(itemId));
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public  ResponseEntity<List<ItemDto>> getAllItems(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("GET /items: Get all items for user {}", userId);
        try{
            return ResponseEntity.ok(itemService.getAllItems(userId));
        } catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text) {
        log.info("GET /items/search?text={}: Search items by text '{}'", text, text);
        try {
            return ResponseEntity.ok(itemService.searchItemsByText(text));
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}