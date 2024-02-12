package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemDao;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    public UserDao userDao;
    private ItemDao itemDao;
    private UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserDao userDao, ItemDao itemDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.itemDao = itemDao;
        this.userMapper = userMapper;
    }


    @Override
    public List<UserResponseDto> getAll() {
        return userDao.findAll().stream()
                .map(u -> userMapper.toDto(u))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUser(Long id) {
        return userMapper.toDto(
                userDao.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", id)))
        );
    }

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {
        try {
            return userMapper.toDto(userDao.save(userMapper.toUser(userCreateDto)));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Duplicate data");
        }
    }

    @Override
    public UserResponseDto update(Long id, UserUpdateDto userUpdateDto) {
        User updatingUser = userDao.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userUpdateDto.setId(id);
        try {
            User updatedUser = userDao.save(userMapper.update(userUpdateDto, updatingUser));
            return userMapper.toDto(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Duplicate data");
        }
    }

    @Override
    public void delete(Long id) {
        if (!userDao.existsById(id)) {
            throw new NotFoundException("Deleting user not found");
        }
        itemDao.deleteByOwner(id);
        userDao.deleteById(id);
    }
}
