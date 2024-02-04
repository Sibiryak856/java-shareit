package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailDuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    public UserStorage userStorage;
    private ItemStorage itemStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }


    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User getUser(Long id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", id)));
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
    public User update(Long id, UserUpdateDto userUpdateDto) {
        User updatingUser = userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (userUpdateDto.getEmail() != null) {
            if (!updatingUser.getEmail().equalsIgnoreCase(userUpdateDto.getEmail())) {
                if (userStorage.getAll().stream()
                        .anyMatch(u -> u.getEmail().equalsIgnoreCase(userUpdateDto.getEmail()))) {
                    throw new EmailDuplicateException
                            (String.format("User with this email: %s already exists", userUpdateDto.getEmail()));
                }
            }
        }
        userStorage.update(UserMapper.INSTANCE.update(userUpdateDto, updatingUser));
        return userStorage.getUser(id).get();
    }

    @Override
    public void delete(Long id) {
        User deletingUser = userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Deleting user not found"));
        itemStorage.getAll().forEach(item -> {
            if (item.getOwner().equals(deletingUser)) {
                itemStorage.delete(item.getId());
            }
        });
        userStorage.delete(id);
    }
}
