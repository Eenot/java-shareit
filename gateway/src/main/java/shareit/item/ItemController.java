package shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shareit.validator.ItemValidator;
import shareit.validator.PageableValidator;

import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final PageableValidator pageableValidator;
    private final ItemValidator itemValidator;
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        itemValidator.validateItemData(itemDto);
        log.debug("Gateway: Создание элемента {}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable @Positive long itemId, @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        itemValidator.validateItemDataUpdate(itemDto);
        log.debug("Gateway: Обновление элемента с id {}", itemId);
        itemDto.setId(itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive long itemId, @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.debug("Gateway: Получение элемента с id : {} ", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getUserItems(@RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        pageableValidator.checkingPageableParams(from, size);
        log.debug("Gateway: Получение всех вещей пользователя с id {}", userId);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearching(@RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      @RequestParam String text,
                                                      @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        pageableValidator.checkingPageableParams(from, size);
        log.debug("Gateway: Получение вещей при помощи поиска по запросу: {}", text);
        return itemClient.getItemsBySearching(userId, from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createCommentToItem(@PathVariable @Positive Long itemId,
                                                      @RequestBody CommentDto comment,
                                                      @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        itemValidator.validateCommentData(comment);
        log.debug("Gateway: Создание отзыва на вещь от пользователя с id {}", userId);
        return itemClient.addCommentToItem(userId, itemId, comment);
    }
}
