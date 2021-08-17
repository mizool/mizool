package com.github.mizool.core.exception;

/**
 * @deprecated It is designed to be thrown when the entity is syntactically correct (e.g. all non-null properties are set),
 * but could not be processed for semantic reasons (e.g. invalid references to other database entities).
 * But the UnprocessableEntityException doesn't really say why the entity is unprocessable and
 * is generally a bad fit as no client ever really has a chance of handling such a response. Try to avoid using this
 * exception and use a more specific one.
 */
@Deprecated
public class UnprocessableEntityException extends RuntimeException
{
    public UnprocessableEntityException()
    {
        super();
    }

    public UnprocessableEntityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public UnprocessableEntityException(String message)
    {
        super(message);
    }

    public UnprocessableEntityException(Throwable cause)
    {
        super(cause);
    }
}
