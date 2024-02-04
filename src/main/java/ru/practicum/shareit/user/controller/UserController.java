package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDto> getAll() {
        log.info("Request received: GET /users");
        List<User> users = userService.getAll();
        log.info("Request GET /users processed: {}", users);
        return users.stream()
                .map(u -> UserMapper.INSTANCE.toDto(u))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        log.info("Request received: GET /users/id={}", id);
        User user = userService.getUser(id);
        log.info("Request GET /users/id processed: {}", user);
        return UserMapper.INSTANCE.toDto(user);
    }

    @PostMapping
    public UserResponseDto create(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("Request received: POST /users: {}", userCreateDto);
        User createdUser = userService.create(UserMapper.INSTANCE.toUser(userCreateDto));
        log.info("Request POST /users processed: user={} is created", createdUser);
        return UserMapper.INSTANCE.toDto(createdUser);
    }

    @PatchMapping("/{id}")
    public UserResponseDto update(@PathVariable Long id,
                                  @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Request received: PATCH /users: {}", userUpdateDto);
        User updatedUser = userService.update(id, userUpdateDto);
        log.info("Request PATCH /users processed: user: {} is updated", updatedUser);
        return UserMapper.INSTANCE.toDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Request received: DELETE /users/id={}", id);
        userService.delete(id);
    }
}
