// ItemRequestController.java (Controller - in ru.practicum.request)
package ru.practicum.itemrequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper itemRequestMapper;


    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {  // Get requester ID from header
        log.info("Received POST request to create ItemRequest from user {}: {}", userId, itemRequestDto);
        return itemRequestMapper.toDto(itemRequestService.create(itemRequestMapper.toEntity(itemRequestDto), userId));
    }
    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET request to retrieve ItemRequest {} for user {}", requestId, userId);
        return itemRequestMapper.toDto(itemRequestService.getById(requestId, userId));
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET request to retrieve all ItemRequests for user {}", userId);
        return itemRequestMapper.toDtoList(itemRequestService.getAllByUser(userId));
    }
    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestHeader("X-Sharer-User-Id") Long userId){
        log.info("Received GET request to retrieve all ItemRequests pageable for user {}", userId);
        return itemRequestMapper.toDtoList(itemRequestService.getAll(from, size, userId));
    }
    // You could add a DELETE endpoint, but typically you don't update requests, just create and retrieve.

}