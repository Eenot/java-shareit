package shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shareit.validator.ItemRequestValidator;
import shareit.validator.PageableValidator;

import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final PageableValidator pageableValidator;
    private final ItemRequestValidator itemRequestValidator;
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestBody ItemRequestDto requestDto,
                                                @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        itemRequestValidator.validateItemRequestData(requestDto);
        log.debug("Gateway: Создание запроса на вещь: {}", requestDto);
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItemsWithResponses(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.debug("Gateway: Получение списка запросов пользователя!");
        return requestClient.getAllUserItemsWithResponses(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllCreatedRequests(@RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        pageableValidator.checkingPageableParams(from, size);
        log.debug("Gateway: Получение списка всех созданных запросов!");
        return requestClient.getAllCreatedRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable @Positive Long requestId, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.debug("Gateway: Получение запроса на вещь с Id: {}", requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}
