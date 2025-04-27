package ru.practicum.itemrequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.dto.ItemRequestResponseDto;
import ru.practicum.dto.ItemRequestWithItemsDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.util.Constants;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated // Add this for validation
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper itemRequestMapper;

    @PostMapping
    public ResponseEntity<ItemRequestResponseDto> create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                         @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Received POST request to create ItemRequest from user {}: {}", userId, itemRequestDto);
        try {
            ItemRequestResponseDto createdRequest = itemRequestMapper.toResponseDto(itemRequestService.create(itemRequestMapper.toEntity(itemRequestDto), userId));
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestWithItemsDto> getById(@PathVariable Long requestId,
                                                           @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Received GET request to retrieve ItemRequest {} for user {}", requestId, userId);
        try {
            ItemRequestWithItemsDto requestDto = itemRequestMapper.toDtoWithItems(itemRequestService.getById(requestId, userId));
            return ResponseEntity.ok(requestDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestWithItemsDto>> getAllByUser(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Received GET request to retrieve all ItemRequests for user {}", userId);
        try {
            List<ItemRequestWithItemsDto> requestDtos = itemRequestMapper.toDtoWithItemsList(itemRequestService.getAllByUser(userId));
            return ResponseEntity.ok(requestDtos);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestWithItemsDto>> getAll(@RequestParam(defaultValue = "0") int from,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("Received GET request to retrieve all ItemRequests pageable for user {}", userId);
        try {
            List<ItemRequestWithItemsDto> requestDtos = itemRequestMapper.toDtoWithItemsList(itemRequestService.getAll(from, size, userId));
            return ResponseEntity.ok(requestDtos);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}