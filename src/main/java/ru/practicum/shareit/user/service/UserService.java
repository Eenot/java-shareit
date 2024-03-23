package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto createUser(UserDto user);

    UserDto getUserById(long id);

    Collection<UserDto> getAllUsers();

    UserDto updateUser(UserDto user);

    void removeUser(long id);
}
