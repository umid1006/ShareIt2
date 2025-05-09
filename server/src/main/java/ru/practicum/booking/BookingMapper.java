package ru.practicum.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.dto.BookingDto;
import ru.practicum.dto.BookingItemDto;
import ru.practicum.item.Item;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BookingMapper {

    @Mapping(target = "item", source = "item", qualifiedByName = "mapItemToBookingItemDto")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "itemId", source = "item.id")
        // Map item ID from entity
    BookingDto toDto(Booking booking);

    @Mapping(target = "item", source = "itemId", qualifiedByName = "idToItem")
    @Mapping(target = "booker", source = "booker.id", qualifiedByName = "idToUser")
    // Changed from bookerId to booker.id
    @Mapping(target = "status", defaultValue = "WAITING")
    Booking toEntity(BookingDto bookingDto);

    @Named("idToItem")
    default Item idToItem(Long itemId) {
        if (itemId == null) return null;
        Item item = new Item();
        item.setId(itemId);
        return item;
    }

    @Named("idToUser")
    default User idToUser(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("mapItemToBookingItemDto")
    default BookingItemDto mapItemToBookingItemDto(Item item) {
        if (item == null) return null;
        return BookingItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}