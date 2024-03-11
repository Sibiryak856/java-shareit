package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item1;
    private Item item2;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("userName")
                .email("user@emailc.com")
                .build();
        item1 = Item.builder()
                .name("NaMe")
                .description("description")
                .available(TRUE)
                .owner(user)
                .build();
        item2 = Item.builder()
                .name("item")
                .description("itemDescription")
                .available(Boolean.FALSE)
                .owner(user)
                .build();
        userRepository.save(user);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @AfterEach
    void cleanDB() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void findAllAvailableBySearch() {

        List<Item> result = itemRepository.findAllAvailableBySearch(
                "name",
                "item",
                TRUE,
                PageRequest.of(5 / 10, 10));

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo(item1.getName());
        assertThat(result.get(0).getAvailable()).isEqualTo(TRUE);
    }
}