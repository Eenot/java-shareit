package ru.practicum.shareit.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

@Component
@RequiredArgsConstructor
public class ItemRequestValidator {

    private final ItemRequestRepository repository;

    public void validateItemRequestId(long requestId) {
        if (requestId < 0) {
            throw new IncorrectDataException("Id не может быть меньше нуля! Запрашиваемый id:" + requestId);
        }
        repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запроса с id " + requestId + " не существует!"));
    }

    public ItemRequest validateItemRequestIdAndReturnIt(long requestId) {
        if (requestId < 0) {
            throw new IncorrectDataException("Id не может быть меньше нуля! Запрашиваемый id:" + requestId);
        }
        return repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запроса с id " + requestId + " не существует!"));
    }

    public void validateRequestData(ItemRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new IncorrectDataException("Описание запроса не может быть пустым!");
        }
    }
}
