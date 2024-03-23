package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, HttpServletRequest request) {
        log.debug("Создание элемента {}", itemDto);
        return itemService.createItem(itemDto, request.getIntHeader("X-Sharer-User-Id"));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto, HttpServletRequest request) {
        log.debug("Обновление элемента с id {}", itemId);
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, request.getIntHeader("X-Sharer-User-Id"));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, HttpServletRequest request) {
        log.debug("Получение элемента с id : {} ", itemId);
        return itemService.getItemById(itemId, request.getIntHeader("X-Sharer-User-Id"));
    }

    @GetMapping()
    public Collection<ItemDto> getUserItems(HttpServletRequest request) {
        log.debug("Получение всех вещей пользователя с id {}", request.getIntHeader("X-Sharer-User-Id"));
        return itemService.getItemsByUserId(request.getIntHeader("X-Sharer-User-Id"));
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsBySearching(@RequestParam String text) {
        log.debug("Получение вещей при помощи поиска по запросу: {}", text);
        return itemService.getItemsBySearching(text);
    }
}
