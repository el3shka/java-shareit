package ru.practicum.shareit.matcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.practicum.shareit.exception.ErrorResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseBodyMatcher {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResultMatcher containsError(String expectedMessage) {
        return result -> {
            String json = result.getResponse().getContentAsString();
            ErrorResponse actualObject = objectMapper.readValue(json, ErrorResponse.class);
            assertThat(actualObject.getError()).isEqualTo(expectedMessage);
        };
    }

    public ResultMatcher containsErrorValid(String expectedMessage) {
        return result -> {
            String json = result.getResponse().getContentAsString();
            List<ErrorResponse> actualObject = objectMapper.readValue(json, new TypeReference<List<ErrorResponse>>() {
            });
            actualObject.forEach(errorResponse -> assertThat(errorResponse.getError()).isEqualTo(expectedMessage));
        };
    }

    public static ResponseBodyMatcher responseBody() {
        return new ResponseBodyMatcher();
    }
}
