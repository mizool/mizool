package com.github.mizool.core.validation;

import java.lang.annotation.Annotation;

import jakarta.validation.ConstraintValidator;

public abstract class ConstraintValidatorAdapter<A extends Annotation, T> implements ConstraintValidator<A, T>
{
    @Override
    public void initialize(A constraintAnnotation)
    {
        // Override when needed
    }
}
