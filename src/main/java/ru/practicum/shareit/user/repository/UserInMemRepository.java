package ru.practicum.shareit.user.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;


@Repository
public class UserInMemRepository implements UserRepository {

    private Long userId = 0L;

    private final Map<Long, User> users = new HashMap<>();

    private final Set<String> usersEmailSet = new HashSet<>();

    @Autowired
    private UserMapper userMapper;

    public UserInMemRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        if (!usersEmailSet.add(user.getEmail())) {
            throw new DuplicateException(String.format("User with this email: %s already exists", user.getEmail()));
        }
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void update(User user) {
        User updatingUser = users.get(user.getId());
        if (user.getEmail() != null) {
            if (!user.getEmail().equalsIgnoreCase(updatingUser.getEmail())) {
                if (!usersEmailSet.add(user.getEmail())) {
                    throw new DuplicateException(
                            String.format("User with this email: %s already exists", user.getEmail())
                    );
                }
                usersEmailSet.remove(updatingUser.getEmail());
            }
        }
        userMapper.update(user, updatingUser);
    }

    @Override
    public void delete(Long id) {
        usersEmailSet.remove(users.get(id).getEmail());
        users.remove(id);
    }
}
