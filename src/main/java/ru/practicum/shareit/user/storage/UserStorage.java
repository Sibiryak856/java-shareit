package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getAll();

    Optional<User> getUser(Long id);

    User create(User user);

    void update(User user);

    void delete(Long id);
}
