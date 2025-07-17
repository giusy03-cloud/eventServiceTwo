package com.dipartimento.eventservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "La data di inizio deve essere precedente o uguale alla data di fine";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
