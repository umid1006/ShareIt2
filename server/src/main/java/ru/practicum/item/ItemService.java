// ItemService.java (Interface)
package ru.practicum.item;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ru.practicum.dto.ItemDto add(ItemDto itemDto, Long ownerId);

    ru.practicum.dto.ItemDto updateInStorage(ItemDto itemDto, Long ownerId, Long itemId);

    List<ru.practicum.dto.ItemDto> getAllItems(Long userId);

    ru.practicum.dto.ItemDto getItemById(Long itemId);

    Boolean isExcludeItemById(Long itemId); // Keep this

    void removeItemById(Long itemId);

    List<ru.practicum.dto.ItemDto> searchItemsByText(String text);

    List<ru.practicum.dto.ItemDto> getItemsByOwner(Long ownerId);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}