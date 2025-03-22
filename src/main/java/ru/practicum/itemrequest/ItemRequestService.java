// ItemRequestService.java (Service Interface - in ru.practicum.request)
package ru.practicum.itemrequest;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(ItemRequest itemRequest, Long userId);
    ItemRequest getById(Long requestId, Long userId);
    List<ItemRequest> getAllByUser(Long userId);
    @Transactional
    List<ItemRequest> getAll(int from, int size, Long userId);
}