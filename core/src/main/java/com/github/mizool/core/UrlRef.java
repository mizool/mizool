package com.github.mizool.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import org.jspecify.annotations.NullMarked;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.errorprone.annotations.Immutable;

/**
 * Safely stores a reference to a URL and allows conversion to {@link URI} and {@link URL} at any time. <br>
 * <br>
 * <b>Features</b>
 * <ul>
 *     <li>
 *         Unlike {@link URL}, this class ensures that invocations of {@link Object#equals(Object) equals()} and
 *         {@link Object#hashCode() hashCode()} never trigger network requests, making this class suitable for
 *         collections. For details, see the SonarQube rule <a href="https://rules.sonarsource.com/java/RSPEC-2112">
 *         "URL.hashCode" and "URL.equals" should be avoided</a>.
 *     </li>
 *     <li>
 *         When constructing a {@code UrlRef} instance, it is ensured that it later can be converted to both a
 *         {@link URI} and a {@link URL}.
 *     </li>
 *     <li>
 *         Creating a {@code UrlRef} never throws checked exceptions.
 *     </li>
 *     <li>
 *         Unlike {@link URI}, an {@code UrlRef} instance can be used to build a child URI - just like {@link URL}
 *         with its {@link URL#URL(URL, String) two-arg constructor}.
 *     </li>
 * </ul>
 */
@Immutable
@NullMarked
@EqualsAndHashCode
public final class UrlRef
{
    private final String spec;

    /**
     * @throws NullPointerException if {@code uri} is {@code null}
     * @throws IllegalArgumentException if the resulting {@code UrlRef} would cause exceptions when converted to {@link URL}
     */
    public UrlRef(@NonNull URI uri)
    {
        verifyValidUrl(uri.toString());
        this.spec = uri.toString();
    }

    /**
     * @throws NullPointerException if {@code url} is {@code null}
     * @throws IllegalArgumentException if the resulting {@code UrlRef} would cause exceptions when converted to {@link URI}
     */
    public UrlRef(@NonNull URL url)
    {
        verifyValidUri(url.toString());
        this.spec = url.toString();
    }

    /**
     * @throws NullPointerException if {@code spec} is {@code null}
     * @throws IllegalArgumentException if the resulting {@code UrlRef} would cause exceptions when converted to {@link URI} or {@link URL}
     */
    public UrlRef(@NonNull String spec)
    {
        verifyValidUri(spec);
        verifyValidUrl(spec);
        this.spec = spec;
    }

    private void verifyValidUri(String spec)
    {
        try
        {
            new URI(spec);
        }
        catch (URISyntaxException e)
        {
            throw createArgumentException(e);
        }
    }

    private void verifyValidUrl(String spec)
    {
        try
        {
            new URL(spec);
        }
        catch (MalformedURLException e)
        {
            throw createArgumentException(e);
        }
    }

    /**
     * Creates a new {@code UrlRef} by combining the URL referenced by this instance (the "context") with the given
     * spec. <br>
     * <br>
     * The new URL is constructed as if using {@link URL#URL(URL, String)}. Note that the result differs based on
     * whether the context has a trailing slash, the spec has a leading slash, both, or none.
     *
     * @throws NullPointerException if {@code spec} is {@code null}
     * @throws IllegalArgumentException if the resulting {@code UrlRef} would cause exceptions when converted to {@link URI} or {@link URL}
     */
    public UrlRef resolve(@NonNull String spec)
    {
        try
        {
            return new UrlRef(new URL(toUrl(), spec));
        }
        catch (MalformedURLException e)
        {
            throw createArgumentException(e);
        }
    }

    private IllegalArgumentException createArgumentException(Exception e)
    {
        return new IllegalArgumentException("Could not construct valid UrlRef", e);
    }

    public URI toUri()
    {
        try
        {
            return new URI(spec);
        }
        catch (URISyntaxException e)
        {
            // Should have been rejected at construction time.
            throw new CodeInconsistencyException(e);
        }
    }

    public URL toUrl()
    {
        try
        {
            return new URL(spec);
        }
        catch (MalformedURLException e)
        {
            // Should have been rejected at construction time.
            throw new CodeInconsistencyException(e);
        }
    }

    public String toString()
    {
        return spec;
    }
}
