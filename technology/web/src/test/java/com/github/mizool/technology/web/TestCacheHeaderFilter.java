/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.mizool.technology.web;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestCacheHeaderFilter
{
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;
    private FilterChain filterChain;
    private CacheHeaderFilter cacheHeaderFilter;

    @BeforeMethod
    public void configure()
    {
        servletRequest = mock(HttpServletRequest.class);
        servletResponse = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        cacheHeaderFilter = new CacheHeaderFilter();
    }

    @Test(dataProvider = "notCached")
    public void testNotCached(String name, String path) throws IOException, ServletException
    {
        when(servletRequest.getServletPath()).thenReturn(path);
        cacheHeaderFilter.doFilter(servletRequest, servletResponse, filterChain);
        verifyNotCached(servletResponse);
    }

    @DataProvider
    public Object[][] notCached()
    {
        return new Object[][]{
            {
                "Index Html", "/index.html"
            }, {
                "Root resources", "/resource.xml"
            }, {
                "Snapshot resources", "/1.0-SNAPSHOT/app.js"
            }
        };
    }

    @Test(dataProvider = "cachedForever")
    public void testCachedForever(String name, String path) throws IOException, ServletException
    {
        when(servletRequest.getServletPath()).thenReturn(path);
        cacheHeaderFilter.doFilter(servletRequest, servletResponse, filterChain);
        verifyCachedForever(servletResponse);
    }

    @DataProvider
    public Object[][] cachedForever()
    {
        return new Object[][]{
            {
                "Static resources", "/1.0/app.js"
            }, {
                "Webjars", "/webjars/requirejs/2.1.20/require.js"
            }
        };
    }

    private void verifyNotCached(HttpServletResponse servletResponse)
    {
        verify(servletResponse).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        verify(servletResponse).setHeader("Pragma", "no-cache");
        verify(servletResponse).setDateHeader("Expires", 0);
    }

    private void verifyCachedForever(HttpServletResponse servletResponse)
    {
        verify(servletResponse, never()).setHeader(eq("Cache-Control"), anyString());
        verify(servletResponse, never()).setHeader(eq("Pragma"), anyString());
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(servletResponse).setDateHeader(eq("Expires"), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isGreaterThan(0);
    }
}