package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.UnsupportedMethodException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.inMemory.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemDto;
import static ru.practicum.shareit.item.dto.mapper.ItemMapper.toItemUpdate;

@Slf4j
@RequiredArgsConstructor
public class ItemServiceInMemoryImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto createItem(ItemDto item, long userId) {
        if (item.getAvailable() == null || item.getDescription() == null || item.getName() == null) {
            throw new EmptyFieldException("Поля не могут быть пусты!");
        }
        if (item.getName().isEmpty() || item.getDescription().isEmpty()) {
            throw new EmptyFieldException("Поля не могут быть пусты!");
        }
        return toItemDto(itemRepository.createItem(toItem(item), userId));
    }

    @Override
    public ItemDto updateItem(ItemDto item, long userId) {
        return toItemDto(itemRepository.updateItem(toItemUpdate(item, itemRepository.getItemById(item.getId())), userId));
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        return toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId, Pageable page) {
        return itemRepository.getItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getItemsBySearching(String text, Pageable page) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.getItemsBySearching(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto checkItemOwner(Long itemId, Long ownerId) {
        throw new UnsupportedMethodException("inMemory checkItemOwner");
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        throw new UnsupportedMethodException("inMemory addCommentToItem");
    }
}
