package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(ItemDto item, long userId);

    ItemDto updateItem(ItemDto item, long userId);

    ItemDto getItemById(long itemId, long userId);

    Collection<ItemDto> getItemsByUserId(long userId);

    Collection<ItemDto> getItemsBySearching(String text);

    ItemDto checkItemOwner(Long itemId, Long ownerId);

    CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto);
}
