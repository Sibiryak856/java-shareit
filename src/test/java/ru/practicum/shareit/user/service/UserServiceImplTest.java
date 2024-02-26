package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;

    private User user2;

    @BeforeEach
    public void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("name")
                .email("name@email.com")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("name2")
                .email("name2@email.com")
                .build();
    }

    @AfterEach
    public void clean() {
        user1 = null;
        user2 = null;
    }
    

    @Test
    void getAll() {

    }

    @Test
    void getUser() {
    }

    @Test
    void create() {
        given(userRepository.save(user1)).willReturn(user1);

        User savedUser = userRepository.save(user1);

        assertThat(savedUser).isNotNull();
    }


    /*.when(mockBookDao.findPublicationDate(Mockito.anyInt()))
            .thenThrow(new DataNotAvailableException("Данные не найдены"));

    final DataNotAvailableException exception = Assertions.assertThrows(
            DataNotAvailableException.class,
            () -> bookService.findPublicationYear(5));

        Assertions.assertEquals("Данные не найдены", exception.getMessage());*/
    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}