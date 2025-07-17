package com.dipartimento.eventservice.validation;

import com.dipartimento.eventservice.dto.EventRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, EventRequest> {

    @Override
    public boolean isValid(EventRequest event, ConstraintValidatorContext context) {
        if (event.getStartDate() == null || event.getEndDate() == null) {
            return true; // Altri validatori gestiranno @NotNull
        }
        return !event.getStartDate().isAfter(event.getEndDate());
    }
}
