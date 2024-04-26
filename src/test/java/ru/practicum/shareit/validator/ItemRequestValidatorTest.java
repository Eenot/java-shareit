package ru.practicum.shareit.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestValidatorTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemRequestValidator itemRequestValidator;

    @Test
    void validateItemRequestId_whenRequestNotExists_thenThrowEntityNotFoundException() {
        when(itemRequestRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Запрос не найден!"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemRequestValidator.validateItemRequestId(1L));

        assertEquals(exception.getMessage(), "Запрос не найден!");
    }


    @Test
    void validateItemRequestId_whenRequestIdLessThanZero_thenThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> itemRequestValidator.validateItemRequestId(-1L));

        assertEquals(exception.getMessage(), "Id не может быть меньше нуля! Запрашиваемый id:-1");
    }

    @Test
    void validateItemRequestIdAndReturns_whenRequestExists_thenReturnRequest() {
        ItemRequest request = new ItemRequest();
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        ItemRequest actual = itemRequestValidator.validateItemRequestIdAndReturnIt(1L);

        assertEquals(actual, request);
    }

    @Test
    void validateItemRequestIdAndReturns_whenRequestNotExists_thenThrowEntityNotFoundException() {
        when(itemRequestRepository.findById(anyLong())).thenThrow(new EntityNotFoundException("Запрос не найден!"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemRequestValidator.validateItemRequestIdAndReturnIt(1L));

        assertEquals(exception.getMessage(), "Запрос не найден!");
    }

    @Test
    void validateItemRequestIdAndReturns_whenRequestIdLessThanZero_thenThrowIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> itemRequestValidator.validateItemRequestIdAndReturnIt(-1L));

        assertEquals(exception.getMessage(), "Id не может быть меньше нуля! Запрашиваемый id:-1");
    }

    @Test
    void validateItemRequestData_whenDataIncorrect_throwIncorrectDataException() {
        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> itemRequestValidator.validateRequestData(new ItemRequestDto()));

        assertEquals(exception.getMessage(), "Описание запроса не может быть пустым!");
    }
}
