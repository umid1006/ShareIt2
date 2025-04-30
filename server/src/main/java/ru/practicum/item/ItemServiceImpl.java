// ItemServiceImpl.java (With Transactional Annotations)
package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingMapper;
import ru.practicum.booking.BookingRepository;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;
import ru.practicum.itemrequest.ItemRequest;
import ru.practicum.itemrequest.ItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;
import ru.practicum.validation.ValidationService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Add this!  Important for read-only operations
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper mapper;
    private final ValidationService validationService;
    private final BookingRepository bookingRepository; // Добавляем репозиторий бронирований
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional // Add this!  Important for write operations
    public ru.practicum.dto.ItemDto updateInStorage(ItemDto itemDto, Long ownerId, Long itemId) {
        if (ownerId == null) {
            throw new ValidateException("Для обновления надо передать ID хозяина вещи.");
        }

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found"));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException(String.format("Вещь %s не принадлежит пользователю с ID = %d.", existingItem.getName(), ownerId));
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Item request with id " + itemDto.getRequestId() + " not found."));
            existingItem.setRequest(itemRequest);
        }

        // Use save() for updates - Spring Data JPA handles it
        Item updatedItem = itemRepository.save(existingItem);
        return mapper.mapToDto(updatedItem);
    }

    @Override
    @Transactional
    public ItemDto add(ItemDto itemDto, Long ownerId) {
        // Validate input
        if (itemDto == null) {
            throw new IllegalArgumentException("ItemDto cannot be null");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found"));

        Item item = mapper.mapToModel(itemDto);
        if (item == null) {
            throw new IllegalStateException("Failed to map ItemDto to Item");
        }

        // Ensure owner is set properly (override the mapped owner if needed)
        item.setOwner(owner);

        validationService.validateItemFields(item);
        Item savedItem = itemRepository.save(item);
        return mapper.mapToDto(savedItem);
    }

    @Override
    public List<ru.practicum.dto.ItemDto> getAllItems(Long userId) {
        // Use the repository method to fetch by owner ID
        return itemRepository.findByOwnerId(userId).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList()).reversed();
    }

    @Override
    public ru.practicum.dto.ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with ID: " + itemId));

        List<Comment> comments = commentRepository.findByItemId(itemId);
        return itemMapper.mapToDtoWithComments(item, comments, commentMapper);
    }

    @Override
    public Boolean isExcludeItemById(Long itemId) {
        return itemRepository.existsById(itemId);
    }

    @Override
    @Transactional // Add this!
    public void removeItemById(Long itemId) {
        // Use existsById for efficiency
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item not found with ID: " + itemId);
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ru.practicum.dto.ItemDto> searchItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        // Use the repository's search method
        return itemRepository.search(text).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList()).reversed();
    }

    @Override
    public List<ru.practicum.dto.ItemDto> getItemsByOwner(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    ru.practicum.dto.ItemDto itemDto = mapper.mapToDto(item);

                    Booking lastBooking = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(item.getId(), now)
                            .stream()
                            .findFirst()
                            .orElse(null);
                    itemDto.setLastBooking(lastBooking != null ? bookingMapper.toDto(lastBooking) : null);

                    Booking nextBooking = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(item.getId(), now)
                            .stream()
                            .findFirst()
                            .orElse(null);
                    itemDto.setNextBooking(nextBooking != null ? bookingMapper.toDto(nextBooking) : null);

                    return itemDto;
                })
                .collect(Collectors.toList()).reversed();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        // 1. Проверить, брал ли пользователь вещь в аренду
        boolean hasBooked = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        if (!hasBooked) {
            throw new ValidateException("User has not booked this item.");
        }

        // 2. Найти вещь и пользователя
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found."));
        var author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Author not found"));

        // 3. Создать и сохранить комментарий
        Comment comment = commentMapper.mapToModel(commentDto, userId, itemId);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        return commentMapper.mapToDto(savedComment);
    }
}