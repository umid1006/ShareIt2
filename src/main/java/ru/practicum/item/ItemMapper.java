package ru.practicum.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.itemrequest.ItemRequest;
import ru.practicum.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommentMapper.class) // Add uses parameter
public interface ItemMapper {

    @Mapping(target = "request", source = "requestId", qualifiedByName = "mapRequestIdToItemRequest")
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "mapOwnerIdToUser")
    Item mapToModel(ItemDto itemDto);

    @Mapping(target = "requestId", source = "request", qualifiedByName = "mapItemRequestToRequestId")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "comments", ignore = true) // We'll handle comments separately
    ItemDto mapToDto(Item item);

    @Named("mapItemRequestToRequestId")
    default Long mapItemRequestToRequestId(ItemRequest request) {
        return request != null ? request.getId() : null;
    }

    // Add this method to map with comments
    default ItemDto mapToDtoWithComments(Item item, List<Comment> comments, CommentMapper commentMapper) {
        ItemDto dto = mapToDto(item);
        dto.setComments(comments.stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    @Named("mapRequestIdToItemRequest")
    default ItemRequest mapRequestIdToItemRequest(Long requestId) {
        if (requestId == null) {
            return null;
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        return itemRequest;
    }

    @Named("mapOwnerIdToUser")
    default User mapOwnerIdToUser(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        User user = new User();
        user.setId(ownerId);
        return user;
    }
}