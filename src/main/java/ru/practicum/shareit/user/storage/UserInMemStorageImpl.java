package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.*;


@Component
@RequiredArgsConstructor
public class UserInMemStorageImpl implements UserStorage {

    private Long userId = 0L;

    private final Map<Long, User> users = new HashMap<>();

    private final Map<Long, Set<Long>> userItems = new HashMap<>();


    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(Long id) {
        return Optional.of(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(++userId);
        users.put(user.getId(), user);
        userItems.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}
