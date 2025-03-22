// booking/BookingMapper.java
package ru.practicum.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.item.Item;
import ru.practicum.user.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "item", source = "itemId", qualifiedByName = "mapItemIdToItem")
    @Mapping(target = "booker", source = "bookerId", qualifiedByName = "mapBookerIdToUser")
    Booking mapToModel(BookingDto bookingDto);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "bookerId", source = "booker.id")
    BookingDto mapToDto(Booking booking);

    @org.mapstruct.Named("mapItemIdToItem")
    default Item mapItemIdToItem(Long itemId) {
        if (itemId == null) {
            return null;
        }
        Item item = new Item();
        item.setId(itemId);
        return item;
    }

    @org.mapstruct.Named("mapBookerIdToUser")
    default User mapBookerIdToUser(Long bookerId) {
        if (bookerId == null) {
            return null;
        }
        User user = new User();
        user.setId(bookerId);
        return user;
    }
}