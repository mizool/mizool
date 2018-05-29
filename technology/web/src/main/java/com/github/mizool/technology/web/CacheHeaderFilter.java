/**
 * Copyright 2017-2018 incub8 Software Labs GmbH
 * Copyright 2017-2018 protel Hotelsoftware GmbH
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
package com.github.mizool.technology.web;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CacheHeaderFilter extends FilterAdapter
{
    private static final Pattern STATIC_ELEMENTS_PATH_PATTERN = Pattern.compile(
        "^/(webjars|\\d+(\\.\\d+)*(-[\\dA-Za-z]+)?)/.+$");

    @Override
    public void doFilter(
        ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException
    {
        if (isStaticPathElement(servletRequest))
        {
            addForeverCacheHeaders(servletResponse);
        }
        else
        {
            addNeverCacheHeaders(servletResponse);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isStaticPathElement(ServletRequest servletRequest)
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = request.getServletPath();
        Matcher staticElementsPathMatcher = STATIC_ELEMENTS_PATH_PATTERN.matcher(path);
        boolean result = (staticElementsPathMatcher.matches() && !path.contains("-SNAPSHOT"));
        return result;
    }

    private void addForeverCacheHeaders(ServletResponse servletResponse)
    {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setDateHeader("Expires", createExpiresTimestamp());
    }

    private long createExpiresTimestamp()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 5);
        long result = calendar.getTimeInMillis();
        return result;
    }

    private void addNeverCacheHeaders(ServletResponse servletResponse)
    {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
