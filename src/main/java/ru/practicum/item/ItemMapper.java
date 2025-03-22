// ItemMapper.java (Corrected - No INSTANCE field)
package ru.practicum.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.itemrequest.ItemRequest;
import ru.practicum.user.User;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    // REMOVE THIS:  ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "request", source = "requestId", qualifiedByName = "mapRequestIdToItemRequest")
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "mapOwnerIdToUser")
    Item mapToModel(ItemDto itemDto);

    @Mapping(target = "requestId", source = "request", qualifiedByName = "mapItemRequestToRequestId")
    @Mapping(target = "ownerId", source = "owner.id")
    ItemDto mapToDto(Item item);

    @org.mapstruct.Named("mapItemRequestToRequestId")
    default Long mapItemRequestToRequestId(ItemRequest request) {
        return request != null ? request.getId() : null;
    }

    @org.mapstruct.Named("mapRequestIdToItemRequest")
    default ItemRequest mapRequestIdToItemRequest(Long requestId) {
        if (requestId == null) {
            return null;
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        return itemRequest;
    }

    @org.mapstruct.Named("mapOwnerIdToUser")
    default User mapOwnerIdToUser(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        User user = new User();
        user.setId(ownerId);
        return user;
    }
}