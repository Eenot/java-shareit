package ru.practicum.shareit.user.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.EmailIsAlreadyRegisteredException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.inMemory.UserRepositoryInMemoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserRepositoryInMemoryImplTest {

    private User user;

    private UserRepositoryInMemoryImpl userRepositoryInMemory;

    @BeforeEach
    public void fillData() {
        userRepositoryInMemory = new UserRepositoryInMemoryImpl();
        user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("name")
                .build();
    }

    @Test
    void create_whenEmailIsUnique_thenReturnUser() {
        User userFromMap = userRepositoryInMemory.createUser(user);

        assertEquals(userFromMap.getId(), 1);
        assertEquals(userFromMap.getName(), user.getName());
    }

    @Test
    void create_whenEmailUnique_thenThrowEmailIsAlreadyRegisteredException() {
        userRepositoryInMemory.createUser(user);

        EmailIsAlreadyRegisteredException exception = assertThrows(EmailIsAlreadyRegisteredException.class,
                () -> userRepositoryInMemory.createUser(user));

        assertEquals(exception.getMessage(), "Пользователь с таким адресом эл. почты уже существует!");
    }

    @Test
    void getById_whenUserExists_thenReturnUser() {
        User userFromMap = userRepositoryInMemory.createUser(user);

        User userGetById = userRepositoryInMemory.getUserById(userFromMap.getId());

        assertEquals(userFromMap.getName(), userGetById.getName());
    }

    @Test
    void getAll() {
        userRepositoryInMemory.createUser(user);
        user.setEmail("newEmail@mail.ru");
        userRepositoryInMemory.createUser(user);

        List<User> users = new ArrayList<>(userRepositoryInMemory.getAll());

        assertEquals(users.size(), 2);
    }

    @Test
    void update_whenUserExists_thenReturnUser() {
        User oldUser = userRepositoryInMemory.createUser(user);
        oldUser.setName("updateName");

        User userUpdate = userRepositoryInMemory.updateUser(oldUser);

        assertEquals(userUpdate.getName(), "updateName");
    }

    @Test
    void delete() {
        User userFromMap = userRepositoryInMemory.createUser(user);
        userRepositoryInMemory.removeUser(userFromMap.getId());

        List<User> users = new ArrayList<>(userRepositoryInMemory.getAll());

        assertEquals(users.size(), 0);
    }
}