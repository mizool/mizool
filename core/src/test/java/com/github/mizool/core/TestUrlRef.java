package com.github.mizool.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestUrlRef
{
    @Test(dataProvider = "urlSpecs")
    public void testUriConstructor(String spec) throws Exception
    {
        URI uri = new URI(spec);
        assertUrlRef(new UrlRef(uri), spec);
    }

    @Test(dataProvider = "urlSpecs")
    public void testUrlConstructor(String spec) throws Exception
    {
        URL url = new URL(spec);
        assertUrlRef(new UrlRef(url), spec);
    }

    @Test(dataProvider = "urlSpecs")
    public void testStringConstructor(String spec) throws Exception
    {
        assertUrlRef(new UrlRef(spec), spec);
    }

    @Test(dataProvider = "contextualUrlSpecs")
    public void testResolve(String contextSpec, String spec, String resultSpec) throws Exception
    {
        UrlRef context = new UrlRef(contextSpec);
        UrlRef result = context.resolve(spec);

        assertUrlRef(result, resultSpec);
    }

    public void assertUrlRef(UrlRef urlRef, String spec) throws URISyntaxException, MalformedURLException
    {
        assertThat(urlRef).isNotNull()
            .hasToString(spec);

        assertThat(urlRef.toUri()).isNotNull()
            .isEqualTo(new URI(spec));

        assertThat(urlRef.toUrl()).isNotNull()
            .isEqualTo(new URL(spec));
    }

    @DataProvider
    private Object[][] urlSpecs()
    {
        return new Object[][]{
            new Object[]{ "http://example.com" },
            new Object[]{ "http://example.com/" },
            new Object[]{ "http://example.com/some" },
            new Object[]{ "http://example.com/some/" },
            new Object[]{ "http://example.com/some/path" },
            new Object[]{ "http://example.com/some/path/" },
            new Object[]{ "http://example.com/some/path/?arg1=value1" },
            new Object[]{ "http://example.com/some/path/?arg1=value1&arg2=value2" }
        };
    }

    @DataProvider
    private Object[][] contextualUrlSpecs()
    {
        return new Object[][]{
            new Object[]{ "http://example.com", "", "http://example.com" },
            new Object[]{ "http://example.com/", "/", "http://example.com/" },

            new Object[]{ "http://example.com/", "foo", "http://example.com/foo" },
            new Object[]{ "http://example.com/", "foo/", "http://example.com/foo/" },
            new Object[]{ "http://example.com/", "/foo", "http://example.com/foo" },
            new Object[]{ "http://example.com/", "/foo/", "http://example.com/foo/" },

            new Object[]{ "http://example.com/bar", "foo", "http://example.com/foo" },
            new Object[]{ "http://example.com/bar", "foo/", "http://example.com/foo/" },
            new Object[]{ "http://example.com/bar", "/foo", "http://example.com/foo" },
            new Object[]{ "http://example.com/bar", "/foo/", "http://example.com/foo/" },

            new Object[]{ "http://example.com/bar/", "foo", "http://example.com/bar/foo" },
            new Object[]{ "http://example.com/bar/", "foo/", "http://example.com/bar/foo/" },
            new Object[]{ "http://example.com/bar/", "/foo", "http://example.com/foo" },
            new Object[]{ "http://example.com/bar/", "/foo/", "http://example.com/foo/" }
        };
    }
}
