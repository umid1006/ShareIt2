package ru.practicum.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentDto mapToDto(Comment comment);

    @Mapping(source = "authorId", target = "author.id")
    @Mapping(source = "itemId", target = "item.id")
    Comment mapToModel(CommentDto commentDto, Long authorId, Long itemId);
}