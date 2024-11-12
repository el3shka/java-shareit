package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDateTime;
import java.util.Objects;

public class StartTimeBeforeEndTimeValidation implements ConstraintValidator<StartBeforeEnd, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object start = new BeanWrapperImpl(value).getPropertyValue("start");
        Object end = new BeanWrapperImpl(value).getPropertyValue("end");
        if (Objects.equals(start, end)) {
            return false;
        }
        if (start instanceof LocalDateTime && end instanceof LocalDateTime) {
            return !((LocalDateTime) end).isBefore((LocalDateTime) start);
        }
        return true;
    }
}
