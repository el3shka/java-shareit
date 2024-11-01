package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userRepository.save(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Test Request");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        itemRequestRepository.save(itemRequest);
    }

    @Test
    void findByIdWithRelations_whenRequestExists_shouldReturnRequestWithRelations() {
        Optional<ItemRequest> result = itemRequestRepository.findByIdWithRelations(itemRequest.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(itemRequest.getId());
        assertThat(result.get().getDescription()).isEqualTo(itemRequest.getDescription());
    }

    @Test
    void findByIdWithRelations_whenRequestNotExists_shouldReturnEmptyOptional() {
        Optional<ItemRequest> result = itemRequestRepository.findByIdWithRelations(999L);

        assertThat(result).isNotPresent();
    }

    @Test
    void findAllByRequesterId_whenRequestsExist_shouldReturnListOfRequests() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequesterId(user.getId(), Sort.by("id"));

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getId()).isEqualTo(itemRequest.getId());
    }

    @Test
    void findAllByRequesterId_whenNoRequestsExist_shouldReturnEmptyList() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("anotheruser@example.com");

        List<ItemRequest> result = itemRequestRepository.findAllByRequesterId(anotherUser.getId(), Sort.by("id"));

        assertThat(result).isEmpty();
    }

    @Test
    void findAllOtherByRequesterId_whenOnlyOneRequestExists_shouldReturnEmptyList() {
        List<ItemRequest> result = itemRequestRepository.findAllOtherByRequesterId(user.getId(), Sort.by("id"));

        assertThat(result).isEmpty();
    }
}

