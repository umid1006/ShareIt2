// ItemRequestServiceImpl.java (Service Implementation - in ru.practicum.request)
package ru.practicum.itemrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.validation.ValidationService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ValidationService validationService;

    @Override
    @Transactional
    public ItemRequest create(ItemRequest itemRequest, Long userId) {
        User requester = validationService.checkExistUserInDB(userId); // Validate user exists
        itemRequest.setRequester(requester); // Set the requester
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequest getById(Long requestId, Long userId) {
        validationService.checkExistUserInDB(userId);
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest with id " + requestId + " not found"));
    }

    @Override
    public List<ItemRequest> getAllByUser(Long userId) {
        validationService.checkExistUserInDB(userId);
        return itemRequestRepository.findAllByRequesterId(userId);
    }

    @Override
    public List<ItemRequest> getAll(int from, int size, Long userId) {
        validationService.checkExistUserInDB(userId);
        return itemRequestRepository.findAllPageable(userId, PageRequest.of(from,size));

    }
}