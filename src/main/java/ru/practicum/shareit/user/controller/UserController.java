package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {

    private final UserServiceImpl userservice;

    @Autowired
    public UserController(UserServiceImpl userservice) {
        this.userservice = userservice;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Request received: GET /users");
        Collection<User> users = userservice.getAll();
        log.info("Request GET /users processed: {}", users);
        return users;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        log.info("Request received: GET /users/id={}", id);
        User user = userservice.getUser(id);
        log.info("Request GET /users/id processed: {}", user);
        return user;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Request received: POST /users: {}", user);
        User createdUser = userservice.create(user);
        log.info("Request POST /users processed: user={} is created", createdUser);
        return createdUser;
    }

    @PatchMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Request received: PATCH /users: {}", user);
        User updatedUser = userservice.update(user);
        log.info("Request PUT /users processed: user: {} is updated", updatedUser);
        return updatedUser;
    }

    @DeleteMapping
    public void delete(@PathVariable Long id) {
        log.info("Request received: DELETE /users/id=", id);
        userservice.delete(id);
    }
}
