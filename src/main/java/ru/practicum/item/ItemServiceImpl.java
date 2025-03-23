// ItemServiceImpl.java (With Transactional Annotations)
package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidateException;
import ru.practicum.itemrequest.ItemRequest;
import ru.practicum.itemrequest.ItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;
import ru.practicum.validation.ValidationService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Add this!  Important for read-only operations
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper mapper;
    private final ValidationService validationService;

    @Override
    @Transactional // Add this!  Important for write operations
    public ItemDto updateInStorage(ItemDto itemDto, Long ownerId, Long itemId) {
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
    @Transactional // Add this!
    public ItemDto add(ItemDto itemDto, Long ownerId) {
        // 1. Fetch the User (owner)
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " not found"));

        // 2. Map DTO to entity
        Item item = mapper.mapToModel(itemDto);

        // 3. Set the owner *entity*
        item.setOwner(owner);

        // 4. Validate
        validationService.validateItemFields(item);

        // 5. Save (Spring Data JPA handles insert/update)
        Item savedItem = itemRepository.save(item);
        return mapper.mapToDto(savedItem);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        // Use the repository method to fetch by owner ID
        return itemRepository.findByOwnerId(userId).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        // Return DTO, not entity
        return itemRepository.findById(itemId)
                .map(mapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Item not found with ID: " + itemId));
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
    public List<ItemDto> searchItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        // Use the repository's search method
        return itemRepository.search(text).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }
}