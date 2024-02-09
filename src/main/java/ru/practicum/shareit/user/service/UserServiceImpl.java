package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    public UserRepository userRepository;
    private ItemRepository itemRepository;
    private UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ItemRepository itemRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.userMapper = userMapper;
    }


    @Override
    public List<UserResponseDto> getAll() {
        return userRepository.getAll().stream()
                .map(u -> userMapper.toDto(u))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUser(Long id) {
        return userMapper.toDto(
                userRepository.getUser(id)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", id)))
        );
    }

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {
        return userMapper.toDto(userRepository.create(userMapper.toUser(userCreateDto)));
    }

    @Override
    public UserResponseDto update(Long id, UserUpdateDto userUpdateDto) {
        User updatingUser = userRepository.getUser(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userUpdateDto.setId(id);
        userRepository.update(userMapper.toUser(userUpdateDto));
        return userMapper.toDto(userRepository.getUser(id).get());
    }

    @Override
    public void delete(Long id) {
        User deletingUser = userRepository.getUser(id)
                .orElseThrow(() -> new NotFoundException("Deleting user not found"));
        itemRepository.deleteAllByUser(id);
        userRepository.delete(id);
    }
}
