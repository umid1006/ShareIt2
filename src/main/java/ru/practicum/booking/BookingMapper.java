package ru.practicum.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.item.Item;
import ru.practicum.item.ItemMapper;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "bookerId", source = "booker.id")
    BookingDto toDto(Booking booking);

    @Mapping(target = "item", source = "itemId", qualifiedByName = "idToItem")
    @Mapping(target = "booker", source = "bookerId", qualifiedByName = "idToUser")
    @Mapping(target = "status", expression = "java(ru.practicum.booking.BookingStatus.WAITING)")
    Booking toEntity(BookingDto bookingDto);

    // Helper methods for ID conversion
    @Named("idToItem")
    default Item idToItem(Long itemId) {
        if (itemId == null) {
            return null;
        }
        Item item = new Item();
        item.setId(itemId);
        return item;
    }

    @Named("idToUser")
    default User idToUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}