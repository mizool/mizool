package com.github.mizool.technology.web;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CacheHeaderFilter extends HttpFilterAdapter
{
    private static final Pattern STATIC_ELEMENTS_PATH_PATTERN = Pattern.compile(
        "^\\/(webjars|\\d+(\\.\\d+)*+(-[\\dA-Za-z]+)?)\\/.+$");

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        if (isStaticPathElement(request))
        {
            addForeverCacheHeaders(response);
        }
        else
        {
            addNeverCacheHeaders(response);
        }
        chain.doFilter(request, response);
    }

    private boolean isStaticPathElement(HttpServletRequest request)
    {
        String path = request.getServletPath();
        Matcher staticElementsPathMatcher = STATIC_ELEMENTS_PATH_PATTERN.matcher(path);
        boolean result = (staticElementsPathMatcher.matches() && !path.contains("-SNAPSHOT"));
        return result;
    }

    private void addForeverCacheHeaders(HttpServletResponse response)
    {
        response.setDateHeader("Expires", createExpiresTimestamp());
    }

    private long createExpiresTimestamp()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 5);
        long result = calendar.getTimeInMillis();
        return result;
    }

    private void addNeverCacheHeaders(HttpServletResponse response)
    {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
