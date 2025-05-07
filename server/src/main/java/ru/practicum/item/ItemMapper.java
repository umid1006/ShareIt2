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

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", source = "requestId", qualifiedByName = "mapRequestIdToItemRequest")
    @Mapping(target = "id", ignore = true)
    default Item mapToModel(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        // Owner will be set manually in the service layer
        // Request is handled by the @Mapping annotation

        return item;
    }

    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    ItemDto mapToDto(Item item);

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