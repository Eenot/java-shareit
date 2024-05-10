package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validator.PageableValidator;

import java.util.Collection;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final PageableValidator pageableValidator;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Создание элемента {}", itemDto);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Обновление элемента с id {}", itemId);
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Получение элемента с id : {} ", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping()
    public Collection<ItemDto> getUserItems(@RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Получение всех вещей пользователя с id {}", userId);
        Pageable page = PageRequest.of(from / size, size);
        return itemService.getItemsByUserId(userId, page);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsBySearching(@RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestParam String text) {
        log.debug("Получение вещей при помощи поиска по запросу: {}", text);
        Pageable page = PageRequest.of(from / size, size);
        return itemService.getItemsBySearching(text, page);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createCommentToItem(@PathVariable Long itemId,
                                          @RequestBody CommentDto comment,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Создание отзыва на вещь от пользователя с id {}", userId);
        return itemService.addCommentToItem(userId, itemId, comment);
    }
}
