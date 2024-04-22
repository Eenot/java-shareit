package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyFieldException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserUpdate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceDbImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new EmptyFieldException("Поле адрес эл. почты не может быть пустым!");
        }
        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Override
    public UserDto getUserById(long id) {
        User userFromRepos = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с id " + id + " не существует!"));
        return toUserDto(userFromRepos);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User userToUpdate = toUserUpdate(userDto, userRepository.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с id " + userDto.getId() + " не существует!")));
        userRepository.save(userToUpdate);
        return toUserDto(userToUpdate);
    }

    @Override
    public void removeUser(long id) {
        User userFromDb = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с id " + id + " не существует!"));
        userRepository.deleteById(userFromDb.getId());
    }
}
