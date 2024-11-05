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
import ru.practicum.shareit.booking.dto.NewBookingDtoRequest;
import ru.practicum.shareit.utils.DataUtils;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.matcher.ResponseBodyMatcher.responseBody;


@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingClient bookingClient;
    private static final String URL = "/bookings";
    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    @DisplayName("Test validation booking functionality. Iten not be null.")
    void givenNewBookingDtoRequest_whenItemIdNull_throwException() throws Exception {
        //given
        NewBookingDtoRequest newBookingDtoRequest = DataUtils.getNewBookingDtoRequestTestTransient(1);
        newBookingDtoRequest.setItemId(null);
        String json = objectMapper.writeValueAsString(newBookingDtoRequest);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, "1")
                .content(json));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody()
                        .containsError("Invalid value of the createBooking.dto.itemId parameter: " +
                                "Item id must not be null"));
    }

    @Test
    @DisplayName("Test validation booking functionality. Start time not be null.")
    void givenNewBookingDtoRequest_whenStartTimeNull_throwException() throws Exception {
        //given
        NewBookingDtoRequest newBookingDtoRequest = DataUtils.getNewBookingDtoRequestTestTransient(1);
        newBookingDtoRequest.setStart(null);
        String json = objectMapper.writeValueAsString(newBookingDtoRequest);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, "1")
                .content(json));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody()
                        .containsError("Invalid value of the createBooking.dto.start parameter: " +
                                "Start time booking must not be null"));
    }

    @Test
    @DisplayName("Test validation booking functionality. Start time in past.")
    void givenNewBookingDtoRequest_whenStartTimePast_throwException() throws Exception {
        //given
        NewBookingDtoRequest newBookingDtoRequest = DataUtils.getNewBookingDtoRequestTestTransient(1);
        newBookingDtoRequest.setStart(LocalDateTime.now().minusDays(1));
        String json = objectMapper.writeValueAsString(newBookingDtoRequest);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, "1")
                .content(json));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody()
                        .containsError("Invalid value of the createBooking.dto.start parameter: " +
                                "Start time cannot be in the past"));
    }

    @Test
    @DisplayName("Test validation booking functionality. End time not be null.")
    void givenNewBookingDtoRequest_whenEndTimeNull_throwException() throws Exception {
        //given
        NewBookingDtoRequest newBookingDtoRequest = DataUtils.getNewBookingDtoRequestTestTransient(1);
        newBookingDtoRequest.setEnd(null);
        String json = objectMapper.writeValueAsString(newBookingDtoRequest);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, "1")
                .content(json));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody()
                        .containsError("Invalid value of the createBooking.dto.end parameter: " +
                                "End time booking must not be null"));
    }

    @Test
    @DisplayName("Test validation booking functionality. End time before start time.")
    void givenNewBookingDtoRequest_whenEndTimeBeforeStartTime_throwException() throws Exception {
        //given
        NewBookingDtoRequest newBookingDtoRequest = DataUtils.getNewBookingDtoRequestTestTransient(1);
        newBookingDtoRequest.setEnd(LocalDateTime.now().minusDays(1));
        newBookingDtoRequest.setStart(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(newBookingDtoRequest);
        //when
        ResultActions result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID, "1")
                .content(json));
        //then
        result.andExpect(status().isBadRequest())
                .andExpect(responseBody()
                        .containsErrorValid("Incorrect time"));
    }
}