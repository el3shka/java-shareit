package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userRepository.save(user);
    }

    @Test
    void whenFindById_thenReturnUser() {
        Optional<User> foundUser = userRepository.findById(user.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
    }

    @Test
    void whenDeleteNonExistentUser_thenReturnZero() {
        int deletedCount = userRepository.delete(999L);

        assertThat(deletedCount).isEqualTo(0);
    }
}
