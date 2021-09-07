package com.github.mizool.core.exception;

/**
 * Thrown when the entity is syntactically correct (e.g. all non-null properties are set), but could not be processed
 * for semantic reasons (e.g. invalid references to other database entities).
 *
 * @deprecated Throwing UnprocessableEntityException doesn't say <i>why</i> the entity is unprocessable and is
 * generally a bad fit as no client ever really has a chance of handling such a response.
 * Instead, consider creating an exception class specific to the respective problem that extends
 * {@link AbstractUnprocessableEntityException}.
 */
@Deprecated
public class UnprocessableEntityException extends AbstractUnprocessableEntityException
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
