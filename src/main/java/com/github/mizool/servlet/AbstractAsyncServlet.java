package com.github.mizool.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractAsyncServlet extends HttpServlet
{
    private static final String DEFAULT_CORE_POOL_SIZE = "3";
    private static final String DEFAULT_MAXIMUM_POOL_SIZE = "10";
    private static final String DEFAULT_THREAD_KEEP_ALIVE_TIME = "30";
    private static final String DEFAULT_THREAD_KEEP_ALIVE_TIME_UNIT = "SECONDS";
    private final BlockingQueue<AsyncContext> requestQueue = new ArrayBlockingQueue<>(20000);
    private final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(20000);

    protected AbstractAsyncServlet()
    {
        int corePoolSize = Integer.parseInt(System.getProperty("async.servlet.core.pool.size", DEFAULT_CORE_POOL_SIZE));
        int maximumPoolSize = Integer.parseInt(
            System.getProperty("async.servlet.maximum.pool.size", DEFAULT_MAXIMUM_POOL_SIZE));
        long keepAliveTime = Long.parseLong(
            System.getProperty("async.servlet.thread.keep.alive.time", DEFAULT_THREAD_KEEP_ALIVE_TIME));
        TimeUnit keepAliveTimeUnit = TimeUnit.valueOf(
            System.getProperty("async.servlet.thread.keep.alive.time.unit", DEFAULT_THREAD_KEEP_ALIVE_TIME_UNIT));
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(20000);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            corePoolSize, maximumPoolSize, keepAliveTime, keepAliveTimeUnit, workQueue);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    public abstract void work(ServletRequest request, ServletResponse response);

    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        requestQueue.add(request.startAsync());
        workQueue.add(processRequest());
    }

    private Runnable processRequest()
    {
        return () -> {
            ArrayList<AsyncContext> requests = new ArrayList<>();
            requestQueue.drainTo(requests);
            requests.parallelStream().forEach(
                asyncContext -> {
                    work(asyncContext.getRequest(), asyncContext.getResponse());
                    asyncContext.complete();
                });
        };
    }
}