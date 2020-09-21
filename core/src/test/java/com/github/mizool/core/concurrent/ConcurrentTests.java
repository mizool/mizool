/*
 * Copyright 2018-2020 incub8 Software Labs GmbH
 * Copyright 2018-2020 protel Hotelsoftware GmbH
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
package com.github.mizool.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

@UtilityClass
class ConcurrentTests
{
    public final int TEST_TIMEOUT = 5000;

    @Value
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private class StreamItem
    {
        long duration;
        Object value;
    }

    public final class Suite
    {
        private final List<StreamItem> streamItems;
        private final AtomicInteger started;
        private final AtomicInteger running;
        private final AtomicInteger maximumConcurrent;
        private final AtomicInteger finished;
        private final ScheduledExecutorService executorService;

        private Suite(int corePoolSize)
        {
            streamItems = new ArrayList<>();
            started = new AtomicInteger();
            running = new AtomicInteger();
            maximumConcurrent = new AtomicInteger();
            finished = new AtomicInteger();
            executorService = Executors.newScheduledThreadPool(corePoolSize);
        }

        public void tearDown()
        {
            executorService.shutdown();
        }

        public void addItems(long... durations)
        {
            LongStream.of(durations)
                .mapToObj(duration -> new StreamItem(duration, mock(Object.class)))
                .forEach(streamItems::add);
        }

        public Stream<ListenableFuture<Object>> stream()
        {
            return streamItems.stream()
                .map(this::toFuture);
        }

        private ListenableFuture<Object> toFuture(StreamItem streamItem)
        {
            started.incrementAndGet();
            int nowRunning = running.incrementAndGet();
            maximumConcurrent.accumulateAndGet(nowRunning, Math::max);
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

    public Suite suite(int corePoolSize)
    {
        return new Suite(corePoolSize);
    }
}