package shareit.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shareit.exception.IncorrectDataException;
import shareit.request.ItemRequestDto;

@Component
@RequiredArgsConstructor
public class ItemRequestValidator {

    public void validateItemRequestData(ItemRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new IncorrectDataException("Описание запроса не может быть пустым!");
        }
    }
}
