package com.github.mizool.technology.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        assertThat(argumentCaptor.getValue()).isPositive();
    }
}
