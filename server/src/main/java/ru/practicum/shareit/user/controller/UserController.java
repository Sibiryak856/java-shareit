package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Request received: GET /users");
        List<UserDto> users = userService.getAll();
        log.info("Request GET /users processed: {}", users);
        return users;
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("Request received: GET /users/id={}", id);
        UserDto user = userService.getUser(id);
        log.info("Request GET /users/id processed: {}", user);
        return user;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto create(@RequestBody UserCreateDto userCreateDto) {
        log.info("Request received: POST /users: {}", userCreateDto);
        UserDto createdUser = userService.create(userCreateDto);
        log.info("Request POST /users processed: user={} is created", createdUser);
        return createdUser;
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Request received: PATCH /users: {}", userUpdateDto);
        UserDto updatedUser = userService.update(id, userUpdateDto);
        log.info("Request PATCH /users processed: user: {} is updated", updatedUser);
        return updatedUser;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Request received: DELETE /users/id={}", id);
        userService.delete(id);
    }
}
