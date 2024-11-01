package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@Rollback
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;


    private Item item1;
    private Item item2;
    private User user1;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("User 1");
        user1.setEmail("jw3pF@example.com");
        userRepository.save(user1);

        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description for Item 1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description for Item 2");
        item2.setAvailable(false);
        item2.setOwner(user1);

        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void findAllByOwnerId_shouldReturnItemsForGivenOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerId(user1.getId());

        assertThat(items).hasSize(2)
                .contains(item1, item2);
    }

    @Test
    void findByOwnerId_shouldReturnItemsWithRelations() {
        List<Item> items = itemRepository.findByOwnerId(user1.getId(), Sort.by("name"));

        assertThat(items).hasSize(2)
                .contains(item1, item2);
    }

    @Test
    void search_shouldReturnItemsByNameOrDescription() {
        List<Item> items = itemRepository.search("Item 1");

        assertThat(items).hasSize(1)
                .contains(item1);
    }

    @Test
    void existsByOwnerId_shouldReturnTrueIfOwnerExists() {
        boolean exists = itemRepository.existsByOwnerId(user1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void findByIdWithRelations_shouldReturnItemWithRelations() {
        Optional<Item> foundItem = itemRepository.findByIdWithRelations(item1.getId());

        assertThat(foundItem).isPresent()
                .contains(item1);
    }

    @Test
    void findByNameOrDescription_shouldReturnAvailableItemsByNameOrDescription() {
        List<Item> items = itemRepository.findByNameOrDescription("Item", Sort.unsorted());

        assertThat(items).hasSize(1)
                .contains(item1);
    }
}

