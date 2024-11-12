package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.NewBookingDtoRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.utils.DataUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.matcher.ResponseBodyMatcher.responseBody;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    private static final String URL = "/bookings";
    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    @DisplayName("Test create booking functionality")
    void givenBookingDto_whenCreateBooking_thenCreateBookingDtoReturned() throws Exception {
        //given
        NewBookingDtoRequest bookingDtoRequest = DataUtils.getNewBookingDtoRequestTestTransient(1);
        String json = objectMapper.writeValueAsString(bookingDtoRequest);
        BookingDtoResponse response = DataUtils.getBookingDtoResponseTestPersistence(1);
        given(bookingService.createBooking(any(NewBookingDtoRequest.class))).willReturn(response);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .content(json));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    @DisplayName("Test create booking wrong user id functionality")
    void givenBookingDto_whenCreateBookingWithWrongUser_thenThrowException() throws Exception {
        //given
        NewBookingDtoRequest bookingDtoRequest = DataUtils.getNewBookingDtoRequestTestTransient(1);
        String json = objectMapper.writeValueAsString(bookingDtoRequest);
        given(bookingService.createBooking(any(NewBookingDtoRequest.class)))
                .willThrow(new NotFoundException("User not found"));
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .content(json));
        //then
        result.andExpect(status().isNotFound())
                .andExpect(responseBody().containsError("User not found"));
    }

    @Test
    @DisplayName("Test create booking wrong item id functionality")
    void givenBookingDto_whenCreateBookingWithWrongItem_thenThrowException() throws Exception {
        //given
        NewBookingDtoRequest bookingDtoRequest = DataUtils.getNewBookingDtoRequestTestTransient(1);
        String json = objectMapper.writeValueAsString(bookingDtoRequest);
        given(bookingService.createBooking(any(NewBookingDtoRequest.class)))
                .willThrow(new NotFoundException("Item not found"));
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .content(json));
        //then
        result.andExpect(status().isNotFound())
                .andExpect(responseBody().containsError("Item not found"));
    }

    @Test
    @DisplayName("Test approved booking functionality")
    void givenBookingDto_whenApprovedBooking_thenBookingDtoResponseReturned() throws Exception {
        //given
        BookingDtoResponse response = DataUtils.getBookingDtoResponseTestPersistence(1);
        response.setStatus("APPROVED");
        given(bookingService.approveBooking(anyLong(), anyLong())).willReturn(response);
        //when
        ResultActions result = mockMvc.perform(patch(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .param("approved", "true"));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
        verify(bookingService, never()).rejectBooking(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Test reject booking functionality")
    void givenBookingDto_whenRejectBooking_thenBookingDtoResponseReturned() throws Exception {
        //given
        BookingDtoResponse response = DataUtils.getBookingDtoResponseTestPersistence(1);
        given(bookingService.rejectBooking(anyLong(), anyLong())).willReturn(response);
        //when
        ResultActions result = mockMvc.perform(patch(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .param("approved", "false"));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.status").value("REJECTED"));
        verify(bookingService, never()).approveBooking(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Test get booking by owner or booker functionality")
    void givenBookingDto_whenGetBookingOfOwnerOrBooker_thenBookingDtoResponseReturned() throws Exception {
        //given
        BookingDtoResponse response = DataUtils.getBookingDtoResponseTestPersistence(1);
        given(bookingService.getBookingByIdOfBookerOrOwner(anyLong(), anyLong())).willReturn(response);
        //when
        ResultActions result = mockMvc.perform(get(URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1));
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    @DisplayName("Test get all bookings by booker functionality")
    void givenBookingDto_whenGetBookingsByBooker_thenBookingDtoResponseReturned() throws Exception {
        //given
        BookingDtoResponse response1 = DataUtils.getBookingDtoResponseTestPersistence(1);
        BookingDtoResponse response2 = DataUtils.getBookingDtoResponseTestPersistence(1);
        List<BookingDtoResponse> responseList = List.of(response1, response2);
        given(bookingService.getAllBookingsByBooker(anyLong(), any(BookingState.class))).willReturn(responseList);
        //when
        ResultActions result = mockMvc.perform(get(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .param("state", "ALL"));
        //then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test get all bookings by owner functionality")
    void givenBookingDto_whenGetBookingsByOwner_thenBookingDtoResponseReturned() throws Exception {
        //given
        BookingDtoResponse response1 = DataUtils.getBookingDtoResponseTestPersistence(1);
        BookingDtoResponse response2 = DataUtils.getBookingDtoResponseTestPersistence(1);
        List<BookingDtoResponse> responseList = List.of(response1, response2);
        given(bookingService.getAllBookingsByOwner(anyLong(), any(BookingState.class))).willReturn(responseList);
        //when
        ResultActions result = mockMvc.perform(get(URL + "/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, 1)
                .param("state", "ALL"));
        //then
        result.andExpect(status().isOk());
    }

    void given6UserDto_whengetBookingByIdOfBookerOrOwner_thenUsersReturned() throws Exception {
        //given

        //when

        //then

    }

    void given5UserDto_whenGetAllUsers_thenUsersReturned() throws Exception {
        //given

        //when

        //then

    }

    void given4UserDto_whenGetAllUsers_thenUsersReturned() throws Exception {
        //given

        //when

        //then

    }

    void given3UserDto_whenGetAllUsers_thenUsersReturned() throws Exception {
        //given

        //when

        //then

    }

    void given2UserDto_whenGetAllUsers_thenUsersReturned() throws Exception {
        //given

        //when

        //then

    }

    void given1UserDto_whenGetAllUsers_thenUsersReturned() throws Exception {
        //given

        //when

        //then

    }
}