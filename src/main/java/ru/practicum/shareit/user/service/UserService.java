package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserService {

    Collection<User> getAll();

    User getUser(Long id);

    User create(User user);

    User update(User user);

    void delete(Long id);

}
