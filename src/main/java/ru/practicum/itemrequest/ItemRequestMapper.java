// ItemRequestMapper.java (Mapper - in ru.practicum.request) -- MAPPER!
package ru.practicum.itemrequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.user.UserMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ItemRequestMapper {

    ItemRequestDto toDto(ItemRequest itemRequest);
    @Mapping(target = "requester", ignore = true) // Ignore requester during mapping TO entity
    ItemRequest toEntity(ItemRequestDto itemRequestDto);
    List<ItemRequestDto> toDtoList(List<ItemRequest> itemRequests);

}