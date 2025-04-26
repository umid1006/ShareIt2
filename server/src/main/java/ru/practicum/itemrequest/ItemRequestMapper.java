package ru.practicum.itemrequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.item.Item;
import ru.practicum.item.ItemMapper;
import ru.practicum.user.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface ItemRequestMapper {

    @Mapping(target = "items", source = "items")
    ItemRequestDto toDto(ItemRequest itemRequest);

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "items", ignore = true)
    ItemRequest toEntity(ItemRequestDto itemRequestDto);

    List<ItemRequestDto> toDtoList(List<ItemRequest> itemRequests);

    /**
     * Maps List<Item> to List<ItemDto>
     * This will be automatically used by MapStruct for the items mapping
     */
    default List<ItemDto> mapItemsToItemDtos(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(item -> {
                    ItemDto dto = new ItemDto();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setDescription(item.getDescription());
                    dto.setAvailable(item.getAvailable());
                    dto.setOwnerId(item.getOwner() != null ? item.getOwner().getId() : null);
                    dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}