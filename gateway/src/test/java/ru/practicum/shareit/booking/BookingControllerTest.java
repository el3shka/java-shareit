package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController bookingController;

    private BookItemRequestDto validRequestDto;
    private BookItemRequestDto invalidRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validRequestDto = new BookItemRequestDto(1L,
                LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS));

        invalidRequestDto = new BookItemRequestDto(1L,
                LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void bookItem_validRequest_shouldReturnResponse() {
        long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.bookItem(eq(userId), any())).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.bookItem(userId, validRequestDto);

        assertEquals(expectedResponse, response);
        verify(bookingClient, times(1)).bookItem(userId, validRequestDto);
    }

    @Test
    void bookItem_invalidRequest_shouldThrowValidationException() {
        long userId = 1L;

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingController.bookItem(userId, invalidRequestDto));

        assertEquals("Start time booking must be before end time and both times don't be in the past", exception.getMessage());
        verify(bookingClient, never()).bookItem(anyLong(), any());
    }

    @Test
    void getBooking_validId_shouldReturnResponse() {
        long userId = 1L;
        long bookingId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBooking(userId, bookingId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.getBooking(userId, bookingId);

        assertEquals(expectedResponse, response);
        verify(bookingClient, times(1)).getBooking(userId, bookingId);
    }

    @Test
    void getBookings_validUser_shouldReturnResponse() {
        long userId = 1L;
        String state = "all";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getBookings(userId, state)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.getBookings(userId, state);

        assertEquals(expectedResponse, response);
        verify(bookingClient, times(1)).getBookings(userId, state);
    }

    @Test
    void getBookingsOfOwner_validUser_shouldReturnResponse() {
        long userId = 1L;
        String state = "all";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.getOwnerBookings(userId, state)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.getBookingsOfOwner(userId, state);

        assertEquals(expectedResponse, response);
        verify(bookingClient, times(1)).getOwnerBookings(userId, state);
    }

    @Test
    void changeStatus_validRequest_shouldReturnResponse() {
        long bookingId = 1L;
        long userId = 1L;
        boolean approved = true;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(bookingClient.changeStatus(bookingId, approved, userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingController.changeStatus(bookingId, approved, userId);

        assertEquals(expectedResponse, response);
        verify(bookingClient, times(1)).changeStatus(bookingId, approved, userId);
    }
}


