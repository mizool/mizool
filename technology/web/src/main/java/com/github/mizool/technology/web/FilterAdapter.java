package com.github.mizool.technology.web;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

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
