package ru.practicum.shareit.user.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.inMemory.UserRepositoryInMemoryImpl;
import ru.practicum.shareit.user.service.UserServiceInMemoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.user.dto.mapper.UserMapper.toUserDto;

@ExtendWith(MockitoExtension.class)
class UserServiceInMemoryImplTest {

    private User user;

    @Mock
    UserRepositoryInMemoryImpl userRepositoryInMemory;

    @InjectMocks
    UserServiceInMemoryImpl userServiceInMemory;

    @BeforeEach
    public void fillData() {
        user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("name")
                .build();
    }

    @Test
    void create_whenEmailNotNull_thenReturnUser() {
        when(userRepositoryInMemory.createUser(user)).thenReturn(user);

        UserDto userToCreate = userServiceInMemory.createUser(toUserDto(user));

        assertEquals(userToCreate.getEmail(), user.getEmail());
    }

    @Test
    void getById_whenUserExists_thenReturnUser() {
        when(userRepositoryInMemory.getUserById(anyLong())).thenReturn(user);

        UserDto userGetById = userServiceInMemory.getUserById(1L);
        assertEquals(userGetById.getEmail(), user.getEmail());
    }

    @Test
    void getAll_whenItemExists_thenReturnListOfUser() {
        when(userRepositoryInMemory.getAll()).thenReturn(List.of(user));

        List<UserDto> users = new ArrayList<>(userServiceInMemory.getAllUsers());

        assertEquals(users.size(), 1);
    }

    @Test
    void update_whenUserExists_thenReturnUpdatedUser() {
        User updateUser = user;
        updateUser.setName("updateName");
        when(userRepositoryInMemory.getUserById(anyLong())).thenReturn(user);
        when(userRepositoryInMemory.updateUser(user)).thenReturn(updateUser);

        UserDto updatedFromMap = userServiceInMemory.updateUser(toUserDto(user));

        assertEquals(updatedFromMap.getName(), "updateName");
    }

    @Test
    void delete() {
        when(userRepositoryInMemory.getUserById(anyLong())).thenReturn(user);

        userServiceInMemory.removeUser(1L);

        verify(userRepositoryInMemory, times(1)).removeUser(1L);
    }

}