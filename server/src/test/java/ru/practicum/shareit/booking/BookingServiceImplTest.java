package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.mapper.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.BookingState;
import ru.practicum.shareit.exception.AccessErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private static final long USER_ID = 1L;
    private static final long ITEM_ID = 1L;
    private static final long BOOKING_ID = 1L;

    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemService itemService;
    @Mock
    UserService userService;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = createUser(USER_ID);
        item = createItem(ITEM_ID);
        booking = createBooking(BOOKING_ID, item, user);
    }

    @Test
    void getBookingById_whenUserIsFound_thenReturnedUser() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        Booking actrualBooking = bookingService.getBookingById(BOOKING_ID, USER_ID);

        assertEquals(booking, actrualBooking);

    }

    @Test
    void getBookingById_whenBookingIsNotFound_thenThrowNotFoundException() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(BOOKING_ID, USER_ID));
    }

    @Test
    void createBooking_whenBookingFieldsAreValid_thenBookingIsCreated() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        when(itemService.getItemToBook(ITEM_ID, USER_ID)).thenReturn(item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking actualBooking = bookingService.createBooking(booking);

        assertEquals(booking, actualBooking);

    }

    @Test
    void createBooking_whenStartIsInThePast_thenThrowValidationException() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booking));

        assertEquals("start cannot be in past", exception.getMessage());
    }

    @Test
    void getBookingById_whenUserNotOwnerOrBooker_thenThrowAccessErrorException() {
        booking.getItem().getOwner().setId(3L);
        booking.getBooker().setId(2L);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        AccessErrorException exception = assertThrows(AccessErrorException.class,
                () -> bookingService.getBookingById(BOOKING_ID, 4L));

        assertEquals("Only the owner of the item or the booker can get booking", exception.getMessage());
    }

    @Test
    void getBookingsByBooker_whenStateIsAll_thenReturnBookings() {
        when(bookingRepository.findByBookerId(USER_ID, Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByBooker(USER_ID, BookingState.ALL);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getBookingById_success() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L, 1L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingById_userNotOwnerOrBooker_throwsAccessErrorException() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        AccessErrorException exception = assertThrows(AccessErrorException.class,
                () -> bookingService.getBookingById(1L, 4L));
        assertEquals("Only the owner of the item or the booker can get booking", exception.getMessage());
    }

    // Тест для getBookingsByBooker
    @Test
    void getBookingsByBooker_success() {
        when(bookingRepository.findByBookerId(1L, Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByBooker(1L, BookingState.ALL);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // Тест для changeStatus
    @Test
    void changeStatus_success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = bookingService.changeStatus(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void changeStatus_whenOwnerApproves_thenBookingStatusIsUpdated() {
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = bookingService.changeStatus(BOOKING_ID, USER_ID, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void changeStatus_whenInvalidOwner_thenThrowAccessErrorException() {
        booking.getItem().getOwner().setId(2L);
        when(bookingRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        AccessErrorException exception = assertThrows(AccessErrorException.class,
                () -> bookingService.changeStatus(BOOKING_ID, USER_ID, true));

        assertEquals("Only the owner of the item can change booking status", exception.getMessage());
    }

    @Test
    void changeStatus_approvedIsNull_throwsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.changeStatus(1L, 1L, null));
        assertEquals("Approved status must be provided", exception.getMessage());
    }

    @Test
    void approveBooking_alreadyApproved_throwsIllegalStateException() {
        booking.getItem().getOwner().setId(2L);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bookingService.approveBooking(1L, 1L, true));
        assertEquals("Booking has already been approved or rejected", exception.getMessage());
    }

    @Test
    void getBookingsByBooker_whenStateIsAll_thenReturnAllBookings() {
        User user = createUser(1L);
        Booking booking = createBooking(1L, createItem(1L), user);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        when(bookingRepository.findByBookerId(user.getId(), sort)).thenReturn(List.of(booking));

        List<Booking> result = bookingService.getBookingsByBooker(user.getId(), BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking, result.getFirst());

        verify(bookingRepository).findByBookerId(user.getId(), sort);
    }

    @Test
    void findAllCompleteBookingByUserIdAndItemId_success() {
        User user = createUser(1L);
        Item item = createItem(1L);
        Booking booking = createBooking(1L, item, user);
        when(bookingRepository.findAllCompleteBookingByBookerIdAndItemId(user.getId(), item.getId()))
                .thenReturn(List.of(booking));

        List<Booking> result = bookingService.findAllCompleteBookingByUserIdAndItemId(user.getId(), item.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking, result.getFirst());

        verify(bookingRepository).findAllCompleteBookingByBookerIdAndItemId(user.getId(), item.getId());
    }

    @Test
    void deleteBooking_success() {
        bookingService.deleteBooking(1L);

        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void updateBooking_whenBookingExists_thenUpdateFields() {
        Booking existingBooking = createBooking(1L, createItem(1L), createUser(1L));
        Booking updatedBooking = new Booking();
        updatedBooking.setStart(LocalDateTime.now().plusDays(3));
        updatedBooking.setEnd(LocalDateTime.now().plusDays(4));
        updatedBooking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(existingBooking)).thenReturn(existingBooking);

        Booking result = bookingService.updateBooking(updatedBooking, 1L);

        assertEquals(updatedBooking.getStart(), result.getStart());
        assertEquals(updatedBooking.getEnd(), result.getEnd());
        assertEquals(updatedBooking.getStatus(), result.getStatus());

        verify(bookingRepository).save(existingBooking);
    }

    @Test
    void updateBooking_whenBookingNotFound_thenThrowNotFoundException() {
        Booking updatedBooking = new Booking();
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.updateBooking(updatedBooking, 1L));

        assertEquals("Booking not found", exception.getMessage());
    }

    private User createUser(long id) {
            User user = new User();
            user.setId(1L);
            user.setName("user");
            user.setEmail("Y8w6H@example.com");
            return user;
    }

    private Item createItem(long id) {
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(createUser(USER_ID));
        item.setName("item");
        item.setDescription("description");
        return item;
    }

    private Booking createBooking(long id, Item item, User user) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }
}