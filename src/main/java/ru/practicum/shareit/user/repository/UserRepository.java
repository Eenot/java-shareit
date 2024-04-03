package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    User createUser(User user);

    User getUserById(long id);

    Collection<User> getAll();

    User updateUser(User user);

    void removeUser(long id);
}
