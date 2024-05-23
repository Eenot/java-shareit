package ru.practicum.shareit.validator;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.IncorrectDataException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PageableValidatorTest {

    @Test
    void checkingPageableParams_whenParamsIncorrect_thenThrowIncorrectDataException() {
        PageableValidator validator = new PageableValidator();

        IncorrectDataException exception = assertThrows(IncorrectDataException.class,
                () -> validator.checkingPageableParams(-1, -2));

        assertEquals(exception.getMessage(), "Параметры страниц не могут быть меньше нуля!");
    }
}
