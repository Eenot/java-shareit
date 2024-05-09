package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    public void testHandleUserNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Пользователь не существует!");
        String result = errorHandler.handleUserNotFoundException(exception);
        assertEquals("Ошибка! Объект не найден: Пользователь не существует!", result);
    }

    @Test
    public void testHandleEmailAlreadyExistsException() {
        EmailIsAlreadyRegisteredException exception = new EmailIsAlreadyRegisteredException("Адрес эл. почты уже зарегистрирован");
        String result = errorHandler.handleEmailAlreadyRegisteredException(exception);
        assertEquals("Адрес эл. почты уже зарегистрирован Адрес эл. почты уже зарегистрирован", result);
    }

    @Test
    public void testHandleEmptyFieldException() {
        EmptyFieldException exception = new EmptyFieldException("Пустое поле!");
        String result = errorHandler.handleEmptyFieldException(exception);
        assertEquals("Ошибка! Поле не может быть пустым: Пустое поле!", result);
    }

    @Test
    public void testHandleGatewayHeaderException() {
        IncorrectDataException exception = new IncorrectDataException("Invalid data");
        String result = errorHandler.handleGatewayHeaderException(exception);
        assertEquals("Gateway exception Invalid data", result);
    }

    @Test
    public void testHandleUnsupportedStateException() {
        UnsupportedStatusException exception = new UnsupportedStatusException("Invalid status");
        Map<String, String> result = errorHandler.handleUnsupportedStateException(exception);
        assertEquals("Unknown state: Invalid status", result.get("error"));
    }

    @Test
    public void testHandleUnsupportedMethodException() {
        UnsupportedMethodException exception = new UnsupportedMethodException("Unsupported method");
        String result = errorHandler.handleUnsupportedMethodException(exception);
        assertEquals("Ошибка! Несуществующий метод: Unsupported method", result);
    }
}
