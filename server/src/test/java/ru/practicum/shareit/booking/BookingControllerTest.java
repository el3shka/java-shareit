package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingRetrieveDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.BookingState;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class BookingControllerTest {

    @Mock
    private BookingServiceImpl bookingService;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    void getBooking_success() throws Exception {
        BookingRetrieveDto bookingRetrieveDto = new BookingRetrieveDto();

        when(bookingService.getBookingById(1L, 1L)).thenReturn(new Booking());
        when(bookingMapper.mapToDto(any(Booking.class))).thenReturn(bookingRetrieveDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingService).getBookingById(1L, 1L);
        verify(bookingMapper).mapToDto(any(Booking.class));
    }

    @Test
    void getAllBookingByUser_success() throws Exception {
        BookingRetrieveDto bookingRetrieveDto = new BookingRetrieveDto();
        List<BookingRetrieveDto> bookingList = List.of(bookingRetrieveDto);

        when(bookingService.getBookingsByBooker(1L, BookingState.ALL)).thenReturn(List.of(new Booking()));
        when(bookingMapper.mapToDto(anyList())).thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService).getBookingsByBooker(1L, BookingState.ALL);
        verify(bookingMapper).mapToDto(anyList());
    }

    @Test
    void getAllUserBookings_success() throws Exception {
        BookingRetrieveDto bookingRetrieveDto = new BookingRetrieveDto();
        List<BookingRetrieveDto> bookingList = List.of(bookingRetrieveDto);

        when(bookingService.getBookingsByOwner(1L, BookingState.ALL)).thenReturn(List.of(new Booking()));
        when(bookingMapper.mapToDto(anyList())).thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService).getBookingsByOwner(1L, BookingState.ALL);
        verify(bookingMapper).mapToDto(anyList());
    }

    @Test
    void approveBooking_success() throws Exception {
        BookingRetrieveDto bookingRetrieveDto = new BookingRetrieveDto();

        when(bookingService.approveBooking(1L, 1L, true)).thenReturn(new Booking());
        when(bookingMapper.mapToDto(any(Booking.class))).thenReturn(bookingRetrieveDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingService).approveBooking(1L, 1L, true);
        verify(bookingMapper).mapToDto(any(Booking.class));
    }
}