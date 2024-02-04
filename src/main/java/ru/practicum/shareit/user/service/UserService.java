package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getUser(Long id);

    User create(User user);

    User update(Long id, UserUpdateDto userUpdateDto);

    void delete(Long id);

}
