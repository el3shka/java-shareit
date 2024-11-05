package ru.practicum.shareit.iiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.DataUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BookingIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingMapper bookingMapper;

    @Test
    @DisplayName("Test find booking for owner item or booker functionality")
    void givenIdBooking_whenFindByIdBookingForOwnerOrBooker_thenReturnBooking() {
        //given
        User booker = DataUtils.getUserTestPersistence(2);
        User ownerItem = DataUtils.getUserTestPersistence(1);
        Item item = DataUtils.getItemTestPersistence(1);
        item.setOwner(ownerItem);
        Booking booking = Booking.builder()
                .id(1L)
                .status(Status.APPROVED)
                .startTime(LocalDateTime.of(2024, 8, 16, 0, 0))
                .endTime(LocalDateTime.of(2024, 8, 17, 0, 0))
                .booker(booker)
                .item(item)
                .build();
        BookingDtoResponse expectedDto = bookingMapper.toBookingDtoResponse(booking);
        //when
        BookingDtoResponse bookingByOwnerItem = bookingService.getBookingByIdOfBookerOrOwner(1, 1);
        BookingDtoResponse bookingByBooker = bookingService.getBookingByIdOfBookerOrOwner(1, 2);
        //then
        assertThat(bookingByOwnerItem).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedDto);
        assertThat(bookingByBooker).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Test find all booking by owner with state rejected functionality")
    void givenIdBooking_whenFindAllByOwnerStateRejected_thenReturnBooking() {
        //given
        User booker = DataUtils.getUserTestPersistence(2);
        User ownerItem = DataUtils.getUserTestPersistence(1);
        Item item = DataUtils.getItemTestPersistence(1);
        item.setOwner(ownerItem);
        Booking booking = Booking.builder()
                .id(8L)
                .status(Status.REJECTED)
                .booker(booker)
                .item(item)
                .build();
        BookingDtoResponse expectedDto = bookingMapper.toBookingDtoResponse(booking);
        //when
        List<BookingDtoResponse> allBookingsRejected = bookingService.getAllBookingsByOwner(1, BookingState.REJECTED);
        //then
        assertThat(allBookingsRejected).isNotEmpty()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(expectedDto));

    }

    @Test
    @DisplayName("Test find all booking by owner with state future functionality")
    void givenIdBooking_whenFindAllByOwnerStateFuture_thenReturnBooking() {
        //given
        BookingDtoResponse expectedDto = BookingDtoResponse.builder()
                .id(3)
                .status(Status.APPROVED.toString())
                .build();
        //when
        List<BookingDtoResponse> allBookingsRejected = bookingService.getAllBookingsByOwner(1, BookingState.FUTURE);
        //then
        assertThat(allBookingsRejected).isNotEmpty()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(expectedDto));

    }

    @Test
    @DisplayName("Test find all booking by owner without item functionality")
    void givenIdBooking_whenFindAllByOwnerWithoutItem_thenReturnBooking() {
        //given
        //when
        List<BookingDtoResponse> allBookingsRejected = bookingService.getAllBookingsByOwner(4, BookingState.FUTURE);
        //then
        assertThat(allBookingsRejected).isEmpty();

    }

    @Test
    @DisplayName("Test find all booking by owner with state future functionality")
    void givenIdBooking_whenFindAllByOwnerStatePast_thenReturnBooking() {
        //given
        BookingDtoResponse expectedDto1 = BookingDtoResponse.builder()
                .id(2)
                .status(Status.APPROVED.toString())
                .build();
        BookingDtoResponse expectedDto2 = BookingDtoResponse.builder()
                .id(4)
                .status(Status.APPROVED.toString())
                .build();
        //when
        List<BookingDtoResponse> allBookingsRejected = bookingService.getAllBookingsByOwner(1, BookingState.PAST);
        //then
        assertThat(allBookingsRejected).isNotEmpty()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(expectedDto1, expectedDto2));

    }

    @Test
    @DisplayName("Test create booking  functionality")
    void givenIdBooking_whenCreateBooking_thenReturnBooking() {
        //given
        NewBookingDtoRequest dto = DataUtils.getNewBookingDtoRequestTestTransient(1);

        BookingDtoResponse expectedDto = BookingDtoResponse.builder()
                .id(11)
                .status(Status.WAITING.toString())
                .build();
        //when
        BookingDtoResponse expected = bookingService.createBooking(dto);
        //then
        assertThat(expected).isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedDto);

    }

    @Test
    @DisplayName("Test create booking fail item available functionality")
    void givenIdBooking_whenCreateBookingNotItemAvailable_thenThrowException() {
        //given
        NewBookingDtoRequest dto = DataUtils.getNewBookingDtoRequestTestTransient(3);

        //when

        //then
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(dto));

    }

    @Test
    @DisplayName("Test find all booking by booker with state current functionality")
    void givenIdBooking_whenFindAllByBookerStateCurrent_thenReturnBooking() {
        //given
        BookingDtoResponse expectedDto = BookingDtoResponse.builder()
                .id(5)
                .status(Status.APPROVED.toString())
                .build();

        //when
        List<BookingDtoResponse> allBookingsRejected = bookingService.getAllBookingsByBooker(3, BookingState.CURRENT);
        //then
        assertThat(allBookingsRejected).isNotEmpty()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(expectedDto));

    }

    @Test
    @DisplayName("Test approved booking by user functionality")
    @Transactional
    void givenIdBooking_whenApprovedBookingByUser_thenReturnBooking() {
        //given
        BookingDtoResponse expectedDto = BookingDtoResponse.builder()
                .id(10)
                .status(Status.APPROVED.toString())
                .build();

        //when
        BookingDtoResponse bookingDtoResponse = bookingService.approveBooking(10, 3);
        //then
        assertThat(bookingDtoResponse).isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedDto);

    }

    @Test
    @DisplayName("Test approved booking by not owner item user functionality")
    @Transactional
    void givenIdBooking_whenApprovedBookingByNotOwnerUser_thenThrowException() {
        //given

        //when

        //then
        assertThrows(IllegalArgumentException.class, () -> bookingService.approveBooking(10, 4));

    }

    @Test
    @DisplayName("Test rejected booking by user functionality")
    @Transactional
    void givenIdBooking_whenRejectedBookingByUser_thenReturnBooking() {
        //given
        BookingDtoResponse expectedDto = BookingDtoResponse.builder()
                .id(10)
                .status(Status.REJECTED.toString())
                .build();

        //when
        BookingDtoResponse bookingDtoResponse = bookingService.rejectBooking(10, 3);
        //then
        assertThat(bookingDtoResponse).isNotNull()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedDto);

    }
}
