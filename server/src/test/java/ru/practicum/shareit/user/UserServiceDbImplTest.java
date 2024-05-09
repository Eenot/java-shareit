package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceDbImpl;
import ru.practicum.shareit.validator.UserValidator;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;


@ExtendWith(MockitoExtension.class)
class UserServiceDbImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserValidator userValidator;

    @InjectMocks
    UserServiceDbImpl userService;


    @Test
    void create_whenAllDataIsCorrect_thenReturnCorrectUser() {
        User expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        UserDto actualUser = userService.createUser(toUserDto(expectedUser));

        assertEquals(expectedUser.getEmail(), actualUser.getEmail(), "Адреса эл. почты пользователя отличаются");
        verify(userValidator, times(1))
                .validateUserData(toUserDto(expectedUser));
    }

    @Test
    void create_whenDataIsIncorrect_thenThrowEmptyFieldExceptionException() {
        User expectedUser = new User();
        when(userRepository.save(expectedUser)).thenThrow(new EmptyFieldException("Адрес эл. почты пользователя не может быть пуст!"));

        EmptyFieldException emptyFieldException = assertThrows(EmptyFieldException.class,
                () -> userService.createUser(toUserDto(expectedUser)), "Исключения различаются");
        assertEquals(emptyFieldException.getMessage(), "Адрес эл. почты пользователя не может быть пуст!", "Сообщения различаются");
    }

    @Test
    void getById_whenUserFound_thenReturnUser() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        when(userValidator.validateUserIdAndReturnIt(userId)).thenReturn(expectedUser);

        UserDto actualUser = userService.getUserById(userId);

        assertEquals(toUserDto(expectedUser), actualUser, "Объекты различаются");
    }

    @Test
    void getById_whenUserNotFound_thenThrowEntityNotFoundException() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        when(userValidator.validateUserIdAndReturnIt(userId)).thenThrow(new EntityNotFoundException("Пользователь с таким ID не существует! Id : " + userId));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(userId));

        assertEquals(entityNotFoundException.getMessage(), "Пользователь с таким ID не существует! Id : 0", "сообщения различаются");
    }

    @Test
    void getAll_whenDataExists_thenReturnEmptyCollection() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        Collection<UserDto> users = userService.getAllUsers();

        assertEquals(users.size(), 0, "Размеры страниц различаются");
    }

    @Test
    void update_whenUserExists_thenReturnUpdatedUser() {
        long userId = 0L;
        User updateUser = new User();
        updateUser.setEmail("update@mail.ru");
        when(userValidator.validateUserIdAndReturnIt(userId)).thenReturn(updateUser);

        UserDto actualUser = userService.updateUser(toUserDto(updateUser));

        assertEquals(toUserDto(updateUser), actualUser, "Пользователи различаются");
        verify(userRepository, times(1))
                .save(updateUser);
    }

    @Test
    void update_whenUserNotExists_thenThrowEntityNotFoundException() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("update@mail.ru");
        when(userValidator.validateUserIdAndReturnIt(userId)).thenThrow(new EntityNotFoundException("Пользователь с таким ID не существует! Id : " + userId));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(toUserDto(expectedUser)));

        assertEquals(entityNotFoundException.getMessage(), "Пользователь с таким ID не существует! Id : 0", "Сообщения различаются");
    }

    @Test
    void delete_whenUserExists_thenDeleteUser() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        when(userValidator.validateUserIdAndReturnIt(userId)).thenReturn(expectedUser);

        userService.removeUser(userId);

        verify(userRepository, times(1))
                .deleteById(0L);
    }

    @Test
    void delete_whenUserNotExists_thenThrowEntityNotFoundException() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        when(userValidator.validateUserIdAndReturnIt(userId)).thenThrow(new EntityNotFoundException("Пользователь с таким ID не существует! Id : " + userId));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.removeUser(userId));

        assertEquals(entityNotFoundException.getMessage(), "Пользователь с таким ID не существует! Id : 0", "Сообщения различаются");
    }
}
