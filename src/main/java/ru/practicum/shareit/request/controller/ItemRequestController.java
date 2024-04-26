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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validator.PageableValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static ru.practicum.shareit.constants.Headers.USER_ID;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final PageableValidator pageableValidator;

    @PostMapping
    public ItemRequestDto createNewRequest(@RequestBody ItemRequestDto requestDto, HttpServletRequest request) {
        log.debug("Создание запроса на вещь: {}", requestDto);
        return itemRequestService.createNewRequest(requestDto, (long) request.getIntHeader(USER_ID));
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllUserItemsWithResponses(HttpServletRequest request) {
        log.debug("Получение списка запросов пользователя!");
        return itemRequestService.getAllUserRequestsWithResponses((long) request.getIntHeader(USER_ID));
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllCreatedRequests(@RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(defaultValue = "10") Integer size,
                                                            HttpServletRequest request) {
        pageableValidator.checkingPageableParams(from, size);
        log.debug("Получение списка всех созданных запросов!");
        Pageable page = PageRequest.of(from, size, Sort.by("creationDate").descending());
        return itemRequestService.getAllRequestsToResponse((long) request.getIntHeader(USER_ID), page);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId, HttpServletRequest request) {
        log.debug("Получение запроса на вещь с Id: {}", requestId);
        return itemRequestService.getRequestById((long) request.getIntHeader("X-Sharer-User-Id"), requestId);
    }
}
