package ru.practicum.user;

import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import ru.practicum.dto.UserDto;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    User toUser(@Valid UserDto userDto);
}