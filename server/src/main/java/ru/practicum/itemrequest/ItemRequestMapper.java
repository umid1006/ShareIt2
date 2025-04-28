package ru.practicum.itemrequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.dto.ItemRequestResponseDto;
import ru.practicum.dto.ItemRequestWithItemsDto;
import ru.practicum.dto.ItemResponseDto;
import ru.practicum.item.Item;
import ru.practicum.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)  // ID is auto-generated
    @Mapping(target = "requester", ignore = true)  // Will be set in service
    @Mapping(target = "items", ignore = true)  // Will be populated later
    @Mapping(target = "created", ignore = true)  // Will be set automatically
    ItemRequest toEntity(ItemRequestDto itemRequestDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "created", source = "created")
    ItemRequestResponseDto toResponseDto(ItemRequest itemRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "created", source = "created")
    @Mapping(target = "items", expression = "java(mapItemsToResponseDtos(itemRequest.getItems()))")
    ItemRequestWithItemsDto toDtoWithItems(ItemRequest itemRequest);

    List<ItemRequestWithItemsDto> toDtoWithItemsList(List<ItemRequest> itemRequests);

    default List<ItemResponseDto> mapItemsToResponseDtos(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .map(this::mapToItemResponseDto)
                .collect(Collectors.toList());
    }

    default ItemResponseDto mapToItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .build();
    }
}