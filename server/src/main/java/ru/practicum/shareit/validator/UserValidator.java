package ru.practicum.shareit.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateUserId(long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("Пользователя с header-id " + userId + " не существует!");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с id " + userId + " не существует!"));
    }

    public User validateUserIdAndReturnIt(long userId) {
        if (userId == -1) {
            throw new IncorrectDataException("Пользователя с header-id " + userId + " не существует!");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с id " + userId + " не существует!"));
    }

    public void validateUserData(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new EmptyFieldException("Адрес эл. почты пользователя не может быть пуст!");
        }
    }
}
