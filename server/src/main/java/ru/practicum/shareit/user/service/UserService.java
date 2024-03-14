package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getUser(Long id);

    UserDto create(UserCreateDto userCreateDto);

    UserDto update(Long id, UserUpdateDto userUpdateDto);

    void delete(Long id);

}
