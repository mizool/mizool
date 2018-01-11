package com.github.mizool.core;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Prevents the class from being injected at unqualified injection points.<br>
 * <br>
 * Use this if the bean has to implement a certain interface but should not be injected at corresponding injection
 * points.
 */
@Target(value = { TYPE, PACKAGE, PARAMETER })
@Retention(value = RUNTIME)
@Documented
@Qualifier
public @interface NonDefault
{
}