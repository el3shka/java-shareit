package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.mapper.BookingStatus;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userRepository.save(user);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Item Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
    }

    @Test
    void testSaveUser() {
        Optional<User> foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
    }

    @Test
    void testSaveItem() {
        Optional<Item> foundItem = itemRepository.findById(item.getId());
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getName()).isEqualTo("Test Item");
        assertThat(foundItem.get().getOwner().getId()).isEqualTo(user.getId());
    }

    @Test
    void testSaveBooking() {
        Optional<Booking> foundBooking = bookingRepository.findById(booking.getId());
        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get().getBooker().getId()).isEqualTo(user.getId());
        assertThat(foundBooking.get().getItem().getId()).isEqualTo(item.getId());
        assertThat(foundBooking.get().getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testItemOwnerRelationship() {
        Item foundItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(foundItem.getOwner().getId()).isEqualTo(user.getId());
    }

    @Test
    void testBookingItemRelationship() {
        Booking foundBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(foundBooking.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void testBookingUserRelationship() {
        Booking foundBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(foundBooking.getBooker().getId()).isEqualTo(user.getId());
    }
}


