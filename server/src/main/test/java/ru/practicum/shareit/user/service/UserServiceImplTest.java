package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.utils.DataUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, new UserMapper());
    }

    @Test
    @DisplayName("Test create user functionality")
    public void givenUserDto_whenCreteUser_thenReturnUserDto() {
        //given
        User user = DataUtils.getUserTestPersistence(1);
        given(userRepository.save(any(User.class))).willReturn(user);
        UserDto userDto = DataUtils.getUserDtoTestTransient(1);
        //when
        UserDto userDtoCreated = userService.create(userDto);
        //then
        assertThat(userDtoCreated).isNotNull();
        assertThat(userDtoCreated.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Test create user with duplicated email functionality")
    public void givenUserDto_whenCreteUserWithDuplicateEmail_thenThrowException() {
        //given
        User user = DataUtils.getUserTestPersistence(1);
        given(userRepository.save(any(User.class))).willThrow(new DataIntegrityViolationException(""));
        UserDto userDto = DataUtils.getUserDtoTestTransient(1);
        //when
        assertThrows(DuplicatedDataException.class, () -> userService.create(userDto));
        //then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Test get user by id functionality")
    public void givenUserDto_whenGetUserById_thenUserDtoIsReturned() {
        //given
        User user = DataUtils.getUserTestPersistence(1);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        //when
        UserDto userReturned = userService.getById(user.getId());
        //then
        assertThat(userReturned).isNotNull();
    }

    @Test
    @DisplayName("Test get user by incorrect id functionality")
    public void givenUserDto_whenGetUserByIncorrectId_thenThrowException() {
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        //then
        assertThrows(NotFoundException.class, () -> userService.getById(999));
    }

    @Test
    @DisplayName("Test get all user functionality")
    public void givenUserDto_whenGetAllUsers_thenUserDtoIsReturned() {
        //given
        User user1 = DataUtils.getUserTestPersistence(1);
        User user2 = DataUtils.getUserTestPersistence(2);
        given(userRepository.findAll()).willReturn(List.of(user1, user2));
        //when
        List<UserDto> usersReturned = userService.getAll();
        //then
        assertThat(usersReturned).isNotNull()
                .hasSize(2);
    }

    @Test
    @DisplayName("Test user update functionality")
    public void givenUserDto_whenUpdateUser_thenUserDtoIsUpdated() {
        //given
        String updateName = "update Name";
        User userUpdateName = DataUtils.getUserTestPersistence(1);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(userUpdateName));
        userUpdateName.setName(updateName);
        given(userRepository.save(any(User.class))).willReturn(userUpdateName);
        UserDto userDto = DataUtils.getUserDtoTestPersistence(1);
        userDto.setName(updateName);
        //when
        UserDto userUpdated = userService.update(userDto.getId(), userDto);
        //then
        assertThat(userUpdated).isNotNull();
        assertThat(userUpdated.getName().equals(updateName)).isTrue();
    }

    @Test
    @DisplayName("Test user update with incorrect id functionality")
    public void givenUserDto_whenUpdateUserWithIncorrectId_thenThrowException() {
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        //then
        assertThrows(NotFoundException.class,
                () -> userService.update(1, DataUtils.getUserDtoTestPersistence(1)));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test user update with duplicate email functionality")
    public void givenUserDto_whenUpdateUserWithDuplicateEmail_thenThrowException() {
        //given
        String updateEmail = "update@test.com";
        User user = DataUtils.getUserTestPersistence(1);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(userRepository.save(any(User.class))).willThrow(new DataIntegrityViolationException(""));
        UserDto userDto = DataUtils.getUserDtoTestPersistence(1);
        userDto.setEmail(updateEmail);
        //when
        //then
        assertThrows(DuplicatedDataException.class, () -> userService.update(userDto.getId(), userDto));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Test delete user functionality")
    public void givenUserDto_whenDeleteUser_thenUserIsDeleted() {
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        userService.delete(1L);
        //then
        verify(userRepository, times(1)).deleteById(anyLong());
        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }
}