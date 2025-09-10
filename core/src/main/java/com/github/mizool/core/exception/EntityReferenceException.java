package com.github.mizool.core.exception;

import lombok.experimental.StandardException;

/**
 * Thrown when an entity contains an invalid reference to another entity.
 */
@StandardException
public class EntityReferenceException extends AbstractUnprocessableEntityException
{
}
