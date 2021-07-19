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
@Constraint(validatedBy = { CheckPostDto.class })
@Documented
public @interface PostDto
{
    String message() default "{com.github.mizool.core.validation.PostDto.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
