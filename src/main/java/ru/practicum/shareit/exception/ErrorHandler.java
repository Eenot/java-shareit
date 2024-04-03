package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice()
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(EntityNotFoundException e) {
        return String.format("Ошибка! Объект не найден: %s", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleEmailAlreadyRegisteredException(EmailIsAlreadyRegisteredException e) {
        return String.format("Адрес эл. почты уже зарегистрирован %s", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleEmptFieldException(EmptyFieldException e) {
        return String.format("Ошибка! Поле не может быть пустым: %s", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleGatewayHeaderException(GatewayHeaderException e) {
        return String.format("Gateway exception %s", e.getMessage());
    }
}
