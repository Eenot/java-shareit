package ru.practicum.shareit.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemValidatorTest {

    @Mock
    ItemRepository repository;

    @InjectMocks
    ItemValidator itemValidator;


    @Test
    void validateItemId_whenItemNotExists_thenThrowEntityNotFoundException() {
        when(repository.findById(anyLong())).thenThrow(new EntityNotFoundException("Вещь не найдена!"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemValidator.validateItemId(1L));

        assertEquals(exception.getMessage(), "Вещь не найдена!");
    }

    @Test
    void validateItemId_whenItemIdLessThanZero_thenThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> itemValidator.validateItemId(-1L));

        assertEquals(exception.getMessage(), "Id не может быть меньше нуля! Запрашиваемый id: -1");
    }

    @Test
    void validateItemIdAndReturns_whenItemExists_thenReturnItem() {
        Item item = new Item();
        when(repository.findById(anyLong())).thenReturn(Optional.of(item));

        Item actual = itemValidator.validateItemIdAndReturnIt(1L);

        assertEquals(actual, item);
    }

    @Test
    void validateItemIdAndReturns_whenItemNotExists_thenThrowEntityNotFoundException() {
        when(repository.findById(anyLong())).thenThrow(new EntityNotFoundException("Вещь не найдена!"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemValidator.validateItemIdAndReturnIt(1L));

        assertEquals(exception.getMessage(), "Вещь не найдена!");
    }

    @Test
    void validateItemIdAndReturns_whenItemIdLessThanZero_thenThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> itemValidator.validateItemIdAndReturnIt(-1L));

        assertEquals(exception.getMessage(), "Id не может быть меньше нуля! Запрашиваемый id: -1");
    }

    @Test
    void validateItemData_whenDataIncorrectNullFields_thenThrowEmptyFieldException() {
        EmptyFieldException exception = assertThrows(EmptyFieldException.class,
                () -> itemValidator.validateItemData(new ItemDto()));

        assertEquals(exception.getMessage(), "Обнаружены пустые поля в элементе itemDto!");
    }

    @Test
    void validateItemData_whenDataIncorrectEmptyFields_thenThrowEmptyFieldException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("");
        itemDto.setAvailable(true);
        EmptyFieldException exception = assertThrows(EmptyFieldException.class,
                () -> itemValidator.validateItemData(itemDto));

        assertEquals(exception.getMessage(), "Обнаружены пустые поля в элементе itemDto!");
    }

    @Test
    void validateCommentData_whenDataIsIncorrect_thanThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> itemValidator.validateCommentData(new CommentDto()));

        assertEquals(exception.getMessage(), "Текст отзыва не может быть пуст!");
    }
}
