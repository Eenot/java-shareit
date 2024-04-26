package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.UserValidator;

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
    private final UserValidator userValidator;

    @Override
    public UserDto createUser(UserDto userDto) {
        userValidator.validateUserData(userDto);
        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Override
    public UserDto getUserById(long id) {
        User userFromRepos = userValidator.validateUserIdAndReturnIt(id);
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
        User userToUpdate = toUserUpdate(userDto, userValidator.validateUserIdAndReturnIt(userDto.getId()));
        userRepository.save(userToUpdate);
        return toUserDto(userToUpdate);
    }

    @Override
    public void removeUser(long id) {
        User userFromDb = userValidator.validateUserIdAndReturnIt(id);
        userRepository.deleteById(userFromDb.getId());
    }
}
