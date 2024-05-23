package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createNewRequest(@RequestBody ItemRequestDto requestDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Создание запроса на вещь: {}", requestDto);
        return itemRequestService.createNewRequest(requestDto, userId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllUserItemsWithResponses(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Получение списка запросов пользователя!");
        return itemRequestService.getAllUserRequestsWithResponses(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllCreatedRequests(@RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(defaultValue = "10") Integer size,
                                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Получение списка всех созданных запросов!");
        Pageable page = PageRequest.of(from, size, Sort.by("creationDate").descending());
        return itemRequestService.getAllRequestsToResponse(userId, page);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Получение запроса на вещь с Id: {}", requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}
