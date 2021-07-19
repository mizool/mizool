package com.github.mizool.core.validation.dto;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = { CheckPutDto.class })
@Documented
public @interface PutDto
{
    String message() default "{com.github.mizool.core.validation.PutDto.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
