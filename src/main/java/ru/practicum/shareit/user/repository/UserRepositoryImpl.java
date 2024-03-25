package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailIsAlreadyRegisteredException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> uniqueEmailSet = new HashSet<>();
    private int userId = 0;


    @Override
    public User createUser(User user) {
        user.setId(++userId);
        if (!uniqueEmailSet.add(user.getEmail())) {
            --userId;
            throw new EmailIsAlreadyRegisteredException("Пользователь с таким адресом эл. почты уже существует!");
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getUserById(long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user) {
        String usersEmail = users.get(user.getId()).getEmail();
        if (!user.getEmail().equals(usersEmail)) {
            if (uniqueEmailSet.add(user.getEmail())) {
                uniqueEmailSet.remove(usersEmail);
            } else {
                throw new EmailIsAlreadyRegisteredException("Пользователь с таким адресом эл. почты уже существует!");
            }
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void removeUser(long id) {
        uniqueEmailSet.remove(users.get(id).getEmail());
        users.remove(id);
    }
}
