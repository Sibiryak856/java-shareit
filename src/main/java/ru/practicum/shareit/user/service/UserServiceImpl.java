package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {

    public UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User getUser(Long id) {
        return userStorage.getUser(id).orElseThrow(() -> new NotFoundException(String.format("User id=%id not found", id)));
    }

    @Override
    public User create(User user) {
        if (userStorage.getAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            throw new EmailDuplicateException
                    (String.format("User with this email: %s already exists", user.getEmail()));
        }
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        User updatingUser = userStorage.getUser(user.getId())
                .orElseThrow(() -> new NotFoundException("Updating user not found"));
        userStorage.update(user);
        return userStorage.getUser(user.getId()).get();
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }
}
