package com.github.mizool.technology.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;

/**
 * Avoids empty methods in filters.
 */
public abstract class FilterAdapter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // Override when needed
    }

    @Override
    public void destroy()
    {
        // Override when needed
    }
}
