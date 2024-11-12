package ru.practicum.shareit.matcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.practicum.shareit.exception.ErrorResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseBodyMatcher {
    private final ObjectMapper objectMapper = new ObjectMapper();


    public <T> ResultMatcher containsObjectAsJson(Object expectedObject, Class<T> targetClass) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            T actualObject = objectMapper.readValue(json, targetClass);
            assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
        };
    }

    public <T> ResultMatcher containsListAsJson(Object expectedObject, TypeReference<List<T>> targetType) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            List<T> actualObject = objectMapper.readValue(json, targetType);
            assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
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

    public ResultMatcher containsError(String expectedMessage) {
        return result -> {
            String json = result.getResponse().getContentAsString();
            ErrorResponse actualObject = objectMapper.readValue(json, ErrorResponse.class);
            assertThat(actualObject.getError()).isEqualTo(expectedMessage);
        };
    }

    public static ResponseBodyMatcher responseBody() {
        return new ResponseBodyMatcher();
    }
}
