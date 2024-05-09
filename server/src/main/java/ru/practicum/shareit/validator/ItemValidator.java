package ru.practicum.shareit.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

@Component
@RequiredArgsConstructor
public class ItemValidator {

    private final ItemRepository repository;

    public void validateItemId(long itemId) {
        if (itemId < 0) {
            throw new IncorrectDataException("Id не может быть меньше нуля! Запрашиваемый id: " + itemId);
        }
        repository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещи с id " + itemId + " не существует!"));
    }

    public Item validateItemIdAndReturnIt(long itemId) {
        if (itemId < 0) {
            throw new IncorrectDataException("Id не может быть меньше нуля! Запрашиваемый id: " + itemId);
        }
        return repository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещи с id " + itemId + " не существует!"));
    }

    public void validateItemData(ItemDto itemDto) {
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new EmptyFieldException("Обнаружены пустые поля в элементе itemDto!");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new EmptyFieldException("Обнаружены пустые поля в элементе itemDto!");
        }
    }

    public void validateCommentData(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isEmpty()) {
            throw new IncorrectDataException("Текст отзыва не может быть пуст!");
        }
    }
}
