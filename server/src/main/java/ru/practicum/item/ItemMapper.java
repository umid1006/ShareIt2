package ru.practicum.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.booking.BookingMapper;
import ru.practicum.dto.ItemDto;
import ru.practicum.itemrequest.ItemRequest;
import ru.practicum.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CommentMapper.class, BookingMapper.class})
public interface ItemMapper {

    @Mapping(target = "request", source = "requestId", qualifiedByName = "mapRequestIdToItemRequest")
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "mapOwnerIdToUser")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "available", source = "available")
    Item mapToModel(ItemDto itemDto);


    @Mapping(target = "requestId", source = "request", qualifiedByName = "mapItemRequestToRequestId")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "lastBooking", ignore = true)  // Explicitly ignore
    @Mapping(target = "nextBooking", ignore = true)  // Explicitly ignore
    ru.practicum.dto.ItemDto mapToDto(Item item);

    @Named("mapItemRequestToRequestId")
    default Long mapItemRequestToRequestId(ItemRequest request) {
        return request != null ? request.getId() : null;
    }

    @Named("mapRequestIdToItemRequest")
    default ItemRequest mapRequestIdToItemRequest(Long requestId) {
        if (requestId == null) return null;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        return itemRequest;
    }

    @Named("mapOwnerIdToUser")
    default User mapOwnerIdToUser(Long ownerId) {
        if (ownerId == null) return null;
        User user = new User();
        user.setId(ownerId);
        return user;
    }

    default ItemDto mapToDtoWithComments(Item item, List<Comment> comments, CommentMapper commentMapper) {
        ItemDto dto = mapToDto(item);
        if (comments != null && commentMapper != null) {
            dto.setComments(comments.stream()
                    .map(commentMapper::mapToDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}