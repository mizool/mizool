package com.github.mizool.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

final class ConcurrentTests
{
    public static final int TEST_TIMEOUT = 5000;

    private ConcurrentTests()
    {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @Value
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class StreamItem
    {
        private final long duration;
        private final Object value;
    }

    public abstract static class Suite<F extends Future<?>>
    {
        protected final List<StreamItem> streamItems = new ArrayList<>();
        protected final AtomicInteger started = new AtomicInteger();
        protected final AtomicInteger running = new AtomicInteger();
        protected final AtomicInteger maximumConcurrent = new AtomicInteger();
        protected final AtomicInteger finished = new AtomicInteger();

        public void tearDown()
        {
        }

        public void addItems(long... durations)
        {
            LongStream.of(durations)
                .mapToObj(duration -> new StreamItem(duration, mock(Object.class)))
                .forEach(streamItems::add);
        }

        public Stream<F> stream()
        {
            return streamItems.stream()
                .map(this::toFuture);
        }

        private F toFuture(StreamItem streamItem)
        {
            started.incrementAndGet();
            int nowRunning = running.incrementAndGet();
            maximumConcurrent.accumulateAndGet(nowRunning, Math::max);
            return createFuture(streamItem);
        }

        protected abstract F createFuture(StreamItem streamItem);

        public void assertContainsExpectedResults(Stream<Object> actual)
        {
            List<Object> results = actual.collect(ImmutableList.toImmutableList());
            assertThat(results).containsExactlyInAnyOrderElementsOf(getExpectedResults());
        }

        private List<Object> getExpectedResults()
        {
            return Lists.transform(streamItems, StreamItem::getValue);
        }

        public void assertStartedFutures(int expected)
        {
            assertThat(started.get()).isEqualTo(expected);
        }

        public void assertFinishedFutures(int expected)
        {
            assertThat(finished.get()).isEqualTo(expected);
        }

        public void assertMaximumConcurrentFutures(int expected)
        {
            assertThat(maximumConcurrent.get()).isEqualTo(expected);
        }
    }

    private static final class ListenableFutureSuite extends Suite<ListenableFuture<Object>>
    {
        protected final ScheduledExecutorService executorService;

        private ListenableFutureSuite(int corePoolSize)
        {
            executorService = Executors.newScheduledThreadPool(corePoolSize);
        }

        @Override
        public void tearDown()
        {
            super.tearDown();
            executorService.shutdown();
        }

        @Override
        protected ListenableFuture<Object> createFuture(StreamItem streamItem)
        {
            SettableFuture<Object> future = SettableFuture.create();
            executorService.schedule(finishFuture(future, streamItem), streamItem.getDuration(), TimeUnit.MILLISECONDS);
            return future;
        }

        private Runnable finishFuture(SettableFuture<Object> future, StreamItem streamItem)
        {
            return () -> {
                running.decrementAndGet();
                finished.incrementAndGet();
                future.set(streamItem.getValue());
            };
        }
    }

    private static final class CompletableFutureSuite extends Suite<CompletableFuture<Object>>
    {
        protected final ScheduledExecutorService executorService;

        private CompletableFutureSuite(int corePoolSize)
        {
            executorService = Executors.newScheduledThreadPool(corePoolSize);
        }

        @Override
        public void tearDown()
        {
            super.tearDown();
            executorService.shutdown();
        }

        @Override
        protected CompletableFuture<Object> createFuture(StreamItem streamItem)
        {
            CompletableFuture<Object> future = new CompletableFuture<>();
            executorService.schedule(finishFuture(future, streamItem), streamItem.getDuration(), TimeUnit.MILLISECONDS);
            return future;
        }

        private Runnable finishFuture(CompletableFuture<Object> future, StreamItem streamItem)
        {
            return () -> {
                running.decrementAndGet();
                finished.incrementAndGet();
                future.complete(streamItem.getValue());
            };
        }
    }

    public static Suite<ListenableFuture<Object>> listenableFutureSuite(int corePoolSize)
    {
        return new ListenableFutureSuite(corePoolSize);
    }

    public static Suite<CompletableFuture<Object>> completableFutureSuite(int corePoolSize)
    {
        return new CompletableFutureSuite(corePoolSize);
    }
}
