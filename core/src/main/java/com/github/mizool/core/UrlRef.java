/*
 * Copyright 2020 incub8 Software Labs GmbH
 * Copyright 2020 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import com.github.mizool.core.exception.CodeInconsistencyException;

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
 *         Offers a convenient way to build a child URL regardless if the base URL is a {@link URL}, a {@link URI} or a
 *         {@code UrlRef}.
 *     </li>
 * </ul>
 */
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlRef
{
    /**
     * @throws NullPointerException if {@code url} is {@code null}
     * @throws IllegalArgumentException if the resulting {@code UrlRef} would cause exceptions when converted to {@link URI} or {@link URL}
     */
    public static UrlRef of(@NonNull URL url)
    {
        try
        {
            return new UrlRef(url.toURI());
        }
        catch (URISyntaxException e)
        {
            throw createArgumentException(e);
        }
    }

    private static IllegalArgumentException createArgumentException(Exception e)
    {
        return new IllegalArgumentException("Could not construct valid UrlRef", e);
    }

    /**
     * @throws NullPointerException if {@code context} or {@code spec} is {@code null}
     * @throws IllegalArgumentException if the resulting {@code UrlRef} would cause exceptions when converted to {@link URI} or {@link URL}
     */
    public static UrlRef of(@NonNull URL context, @NonNull String spec)
    {
        try
        {
            return of(new URL(context, spec));
        }
        catch (MalformedURLException e)
        {
            throw createArgumentException(e);
        }
    }

    /**
     * @throws NullPointerException if {@code context} or {@code spec} is {@code null}
     * @throws IllegalArgumentException if the resulting {@code UrlRef} would cause exceptions when converted to {@link URI} or {@link URL}
     */
    public static UrlRef of(@NonNull UrlRef context, @NonNull String spec)
    {
        try
        {
            return of(new URL(context.toUrl(), spec));
        }
        catch (MalformedURLException e)
        {
            throw createArgumentException(e);
        }
    }

    /**
     * @throws NullPointerException if {@code uri} is {@code null}
     * @throws IllegalArgumentException if the resulting {@code UrlRef} would cause exceptions when converted to {@link URI} or {@link URL}
     */
    public static UrlRef of(@NonNull URI uri)
    {
        try
        {
            return new UrlRef(uri.toURL()
                .toURI());
        }
        catch (URISyntaxException | MalformedURLException e)
        {
            throw createArgumentException(e);
        }
    }

    /**
     * @throws NullPointerException if {@code context} or {@code spec} is {@code null}
     * @throws IllegalArgumentException if the resulting {@code UrlRef} would cause exceptions when converted to {@link URI} or {@link URL}
     */
    public static UrlRef of(@NonNull URI context, @NonNull String spec)
    {
        try
        {
            return of(context.toURL(), spec);
        }
        catch (MalformedURLException e)
        {
            throw createArgumentException(e);
        }
    }

    /**
     * @throws NullPointerException if {@code spec} is null
     * @throws IllegalArgumentException if the resulting {@code UrlRef} would cause exceptions when converted to {@link URI} or {@link URL}
     */
    public static UrlRef of(@NonNull String spec)
    {
        try
        {
            return new UrlRef(new URL(spec).toURI());
        }
        catch (URISyntaxException | MalformedURLException e)
        {
            throw new IllegalArgumentException("String cannot be converted to URL and/or URI", e);
        }
    }

    URI reference;

    public URI toUri()
    {
        return reference;
    }

    public URL toUrl()
    {
        try
        {
            return reference.toURL();
        }
        catch (MalformedURLException e)
        {
            // Should have been rejected at construction time.
            throw new CodeInconsistencyException();
        }
    }

    public String toString()
    {
        return reference.toString();
    }
}
