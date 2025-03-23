// ItemService.java (Interface)
package ru.practicum.item;

import java.util.List;

public interface ItemService {
    ItemDto add(ItemDto itemDto, Long ownerId);
    ItemDto updateInStorage(ItemDto itemDto, Long ownerId, Long itemId);
    List<ItemDto> getAllItems(Long userId);
    ItemDto getItemById(Long itemId);
    Boolean isExcludeItemById(Long itemId); // Keep this
    void removeItemById(Long itemId);
    List<ItemDto> searchItemsByText(String text);
}