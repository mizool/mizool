package com.github.mizool.core.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is the opposite of {@link javax.validation.constraints.NotNull} and is intended to document that a field is optional
 */
@Target({ FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
@Documented
public @interface Nullable
{
}
