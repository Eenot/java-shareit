package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.repository.inMemory.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.mapper.UserMapper.*;

@Slf4j
@RequiredArgsConstructor
public class UserServiceInMemoryImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public UserDto createUser(UserDto user) {
        if (user.getEmail() == null) {
            throw new EmptyFieldException("Адрес эл. почты пуст!");
        }
        return toUserDto(userRepository.createUser(toUser(user)));
    }

    @Override
    public UserDto getUserById(long id) {
        checkId(id);
        return toUserDto(userRepository.getUserById(id));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserDto user) {
        checkId(user.getId());
        return toUserDto(userRepository
                .updateUser(toUserUpdate(user, userRepository.getUserById(user.getId()))));
    }

    @Override
    public void removeUser(long id) {
        checkId(id);
        userRepository.removeUser(id);
    }

    private void checkId(long id) {
        if (userRepository.getUserById(id) == null) {
            throw new EntityNotFoundException("Пользователя с id: " + id + " не существует!");
        }
    }
}
