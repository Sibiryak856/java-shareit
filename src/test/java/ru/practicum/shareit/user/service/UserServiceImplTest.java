package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserServiceImpl userService;
    private User user1;
    private User user2;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;
    private UserDto userDtoForUser1;

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
        userCreateDto = UserCreateDto.builder()
                .name("name")
                .email("name@email.com")
                .build();
        userUpdateDto = UserUpdateDto.builder()
                .email("newname@email.com")
                .build();
        userDtoForUser1 = UserDto.builder()
                .id(1L)
                .name("name")
                .email("name@email.com")
                .build();
    }

    @AfterEach
    public void clean() {
        user1 = null;
        user2 = null;
        userCreateDto = null;
        userUpdateDto = null;
        userDtoForUser1 = null;
    }


    @Test
    void getAll_whenAllUsersFound_thenReturnUsers() {
        List<User> users = List.of(user1, user2);
        List<UserDto> usersDto = List.of(
                userDtoForUser1,
                UserDto.builder()
                        .id(user2.getId())
                        .name(user2.getName())
                        .email(user2.getEmail())
                        .build()
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> actualUsers = userService.getAll();

        assertThat(actualUsers).isNotNull();
        assertThat(usersDto).isEqualTo(actualUsers);
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        UserDto actualUser = userService.getUser(user1.getId());

        assertThat(actualUser).isNotNull();
        assertThat(userDtoForUser1).isEqualTo(actualUser);
    }

    @Test
    void getUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 100L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> userService.getUser(userId));

        assertThat(e.getMessage()).isEqualTo(String.format("User id=%d not found", userId));
    }

    @Test
    void create_whenUserValid_thenReturnUserDto() {
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto savedUser = userService.create(userCreateDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(userDtoForUser1);
    }

    @Test
    void update_whenUpdateOnlyEmail_thenReturnUserDto() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        user1.setEmail(userUpdateDto.getEmail());
        userDtoForUser1.setEmail(userUpdateDto.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto updatedUser = userService.update(user1.getId(), userUpdateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(userDtoForUser1);
    }

    /*@Test
    void update_whenUpdateNameAndEmail_thenReturnUserDto() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        userUpdateDto.setName("newName");
        user1.setName(userUpdateDto.getName());
        userDtoForUser1.setName(userUpdateDto.getName());
        user1.setEmail(userUpdateDto.getEmail());
        userDtoForUser1.setEmail(userUpdateDto.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto updatedUser = userService.update(user1.getId(), userUpdateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(userDtoForUser1);
    }

    @Test
    void update_whenUpdateName_thenReturnUserDto() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        userUpdateDto.setName("newName");
        userUpdateDto.setEmail(null);
        user1.setName(userUpdateDto.getName());
        userDtoForUser1.setName(userUpdateDto.getName());
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto updatedUser = userService.update(user1.getId(), userUpdateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(userDtoForUser1);
    }

    @Test
    void update_whenUpdateEmptyField_thenReturnUserDto() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        userUpdateDto.setEmail(null);
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto updatedUser = userService.update(user1.getId(), userUpdateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(userDtoForUser1);
    }*/

    @Test
    void update_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 100L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> userService.update(userId, userUpdateDto));

        assertThat(e.getMessage()).isEqualTo("User not found");
    }

    @Test
    void delete() {
        willDoNothing().given(userRepository).deleteById(any());
        willDoNothing().given(itemRepository).deleteAllByOwnerId(any());

        userService.delete(1L);

        verify(itemRepository, times(1)).deleteAllByOwnerId(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }


    /*
    .when(mockBookDao.findPublicationDate(Mockito.anyInt()))
            .thenThrow(new DataNotAvailableException("Данные не найдены"));

    final DataNotAvailableException exception = Assertions.assertThrows(
            DataNotAvailableException.class,
            () -> bookService.findPublicationYear(5));

        Assertions.assertEquals("Данные не найдены", exception.getMessage());
    @Test
    void update() {
    }

    @Test
    void delete() {
    }*/
}