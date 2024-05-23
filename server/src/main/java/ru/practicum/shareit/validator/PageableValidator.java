package ru.practicum.shareit.validator;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.IncorrectDataException;

@Component
public class PageableValidator {

    public void checkingPageableParams(Integer from, Integer size) {
        if (size < 0 || from < 0) {
            throw new IncorrectDataException("Параметры страниц не могут быть меньше нуля!");
        }
    }
}
